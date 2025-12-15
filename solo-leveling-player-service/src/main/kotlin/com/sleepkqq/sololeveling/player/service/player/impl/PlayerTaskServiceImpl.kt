package com.sleepkqq.sololeveling.player.service.player.impl

import com.sleepkqq.sololeveling.player.config.properties.PlayerLimitsProperties
import com.sleepkqq.sololeveling.player.config.properties.TasksProperties
import com.sleepkqq.sololeveling.player.exception.AccessDeniedException
import com.sleepkqq.sololeveling.player.kafka.producer.GenerateTasksProducer
import com.sleepkqq.sololeveling.player.model.entity.Fetchers
import com.sleepkqq.sololeveling.player.model.entity.Immutables
import com.sleepkqq.sololeveling.player.model.entity.player.Player
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTask
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTaskFetcher
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTaskProps
import com.sleepkqq.sololeveling.player.model.entity.player.TaskTopicItem
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerCompletionTaskView
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerTaskView
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerView
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerBalanceTransactionCause
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerTaskStatus
import com.sleepkqq.sololeveling.player.model.entity.task.Task
import com.sleepkqq.sololeveling.player.model.repository.player.PlayerTaskRepository
import com.sleepkqq.sololeveling.player.service.notification.NotificationCommand
import com.sleepkqq.sololeveling.player.service.notification.NotificationService
import com.sleepkqq.sololeveling.player.service.player.LevelService
import com.sleepkqq.sololeveling.player.service.player.PlayerBalanceService
import com.sleepkqq.sololeveling.player.service.player.PlayerService
import com.sleepkqq.sololeveling.player.service.player.PlayerStaminaService
import com.sleepkqq.sololeveling.player.service.player.PlayerTaskService
import com.sleepkqq.sololeveling.player.service.task.TaskService
import com.sleepkqq.sololeveling.proto.player.RequestPaging
import com.sleepkqq.sololeveling.proto.player.RequestQueryOptions
import org.babyfish.jimmer.ImmutableObjects
import org.babyfish.jimmer.Page
import org.babyfish.jimmer.View
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.babyfish.jimmer.sql.fetcher.Fetcher
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.UUID
import kotlin.reflect.KClass

@Service
@EnableConfigurationProperties(PlayerLimitsProperties::class, TasksProperties::class)
class PlayerTaskServiceImpl(
	private val playerTaskRepository: PlayerTaskRepository,
	private val playerBalanceService: PlayerBalanceService,
	private val playerService: PlayerService,
	private val levelService: LevelService,
	private val taskService: TaskService,
	private val notificationService: NotificationService,
	private val generateTasksProducer: GenerateTasksProducer,
	private val playerLimitsProperties: PlayerLimitsProperties,
	private val playerStaminaService: PlayerStaminaService,
	private val tasksProperties: TasksProperties
) : PlayerTaskService {

	private val log = LoggerFactory.getLogger(javaClass)

	private companion object {
		val ACTIVE_TASKS_STATUSES = setOf(PlayerTaskStatus.PREPARING, PlayerTaskStatus.IN_PROGRESS)
	}

	@Transactional(readOnly = true)
	override fun find(id: UUID, fetcher: PlayerTaskFetcher): PlayerTask? =
		playerTaskRepository.find(id, fetcher)

	@Transactional(readOnly = true)
	override fun <V : View<PlayerTask>> findView(id: UUID, viewType: KClass<V>): V? =
		playerTaskRepository.findView(id, viewType.java)

	@Transactional(readOnly = true)
	override fun find(playerId: Long, taskIds: Collection<UUID>): List<PlayerTask> =
		playerTaskRepository.findByPlayerIdAndTaskIdIn(playerId, taskIds)

	@Transactional
	override fun insertAll(playerTasks: Collection<PlayerTask>) {
		playerTaskRepository.saveEntities(playerTasks, SaveMode.INSERT_ONLY)
	}

	@Transactional(readOnly = true)
	override fun getActiveTasks(playerId: Long): List<PlayerTaskView> =
		playerTaskRepository.findByPlayerIdAndStatusIn(
			playerId,
			ACTIVE_TASKS_STATUSES,
			PlayerTaskView::class.java
		)

	@Transactional(readOnly = true)
	override fun getPreparingTasksForRetry(): List<PlayerTask> =
		playerTaskRepository.findPreparingTasksForRetry()

	@Transactional(readOnly = true)
	override fun getActiveTasks(playerId: Long, fetcher: Fetcher<PlayerTask>): List<PlayerTask> =
		playerTaskRepository.findByPlayerIdAndStatusIn(playerId, ACTIVE_TASKS_STATUSES, fetcher)

	override fun initialize(playerId: Long, order: Int, task: Task): PlayerTask =
		Immutables.createPlayerTask {
			it.setId(UUID.randomUUID())
			it.setStatus(PlayerTaskStatus.PREPARING)
			it.setOrder(order)
			it.setPlayerId(playerId)
			it.setTask(task)
		}

	@Transactional
	override fun skipTask(playerId: Long, id: UUID) {
		val playerTask = get(
			id,
			Fetchers.PLAYER_TASK_FETCHER.allScalarFields()
				.player(
					Fetchers.PLAYER_FETCHER
						.stamina(Fetchers.PLAYER_STAMINA_FETCHER.allScalarFields())
				)
		)

		val player = playerTask.player()!!

		if (player.id() != playerId) {
			throw AccessDeniedException()
		}

		val consumedStamina = playerStaminaService.consumeStamina(
			player.stamina()!!,
			tasksProperties.getSkipCost()
		)
		playerStaminaService.update(consumedStamina)

		setStatus(listOf(playerTask), PlayerTaskStatus.SKIPPED)

		generateTasks(playerId, replaceOrders = setOf(playerTask.order()))
	}

	@Transactional
	override fun completeTask(playerId: Long, id: UUID): Pair<PlayerView, PlayerView> {
		val playerTask = getView(id, PlayerCompletionTaskView::class)
			.toEntity()

		val player = playerTask.player()!!
		val task = playerTask.task()!!

		if (player.id() != playerId) {
			throw AccessDeniedException()
		}

		val consumedStamina = playerStaminaService.consumeStamina(
			player.stamina()!!,
			tasksProperties.getCompleteCost(task.rarity())
		)

		setStatus(listOf(playerTask), PlayerTaskStatus.COMPLETED)

		val updatedBalance = playerBalanceService.deposit(
			player.balance()!!,
			BigDecimal(task.currencyReward()!!),
			PlayerBalanceTransactionCause.TASK_COMPLETION
		)

		val topics = task.topics().map(TaskTopicItem::topic)
		val gainedExperiencePlayer = levelService.gainExperience(player, topics, task.experience()!!)

		val updatedPlayer = playerService.update(
			Immutables.createPlayer(gainedExperiencePlayer) {
				it.setAgility(player.agility() + task.agility()!!)
					.setStrength(player.strength() + task.strength()!!)
					.setIntelligence(player.intelligence() + task.intelligence()!!)
					.setBalance(updatedBalance)
					.setStamina(consumedStamina)
			}
		)

		generateTasks(playerId, updatedPlayer, setOf(playerTask.order()))

		return PlayerView(player) to PlayerView(updatedPlayer)
	}

	@Transactional
	override fun inProgressTasks(tasks: Collection<PlayerTask>) {
		setStatus(tasks, PlayerTaskStatus.IN_PROGRESS, true)
	}

	@Transactional(readOnly = true)
	override fun <V : View<PlayerTask>> searchView(
		playerId: Long,
		options: RequestQueryOptions,
		paging: RequestPaging,
		viewType: KClass<V>
	): Page<V> = playerTaskRepository.searchView(playerId, options, paging, viewType.java)

	@Transactional
	override fun generateTasks(
		playerId: Long,
		player: Player?,
		replaceOrders: Set<Int>
	) {
		val resolvedPlayer = player ?: playerService.get(
			playerId,
			Fetchers.PLAYER_FETCHER.taskTopics(
				Fetchers.PLAYER_TASK_TOPIC_FETCHER
					.taskTopic()
					.active()
					.level(Fetchers.LEVEL_FETCHER.level())
			)
		)

		val activeTasks = getActiveTasks(playerId, Fetchers.PLAYER_TASK_FETCHER.order())

		activeTasks.firstOrNull { it.order() in replaceOrders }
			?.let { throw IllegalArgumentException("Incorrect replacement order ${it.order()}") }

		val maxTasks = playerLimitsProperties.limits.free.tasks.max
		val additionalTasksNeeded = maxTasks - activeTasks.size

		if (additionalTasksNeeded <= 0) {
			log.warn(
				"Invalid state: activeTasksCount={} exceeds maxTasks={} for playerId={}, skipping generation",
				activeTasks, maxTasks, playerId
			)
			return
		}

		val usedOrders = activeTasks.map { it.order() }.toSet()

		val additionalOrders = (0 until maxTasks)
			.filterNot { it in usedOrders }
			.take(additionalTasksNeeded)

		val allNewTasks = (replaceOrders + additionalOrders).map {
			val task = taskService.initialize(resolvedPlayer.taskTopics())
			initialize(playerId, it, task)
		}

		if (allNewTasks.isEmpty()) {
			log.warn("No tasks generated for playerId={}, skipping", playerId)
			return
		}

		log.info(
			"Generated {} tasks for player {} ({} replace, {} new)",
			allNewTasks.size, playerId, replaceOrders.size, allNewTasks.size - replaceOrders.size
		)

		val playerTasksToInsert = taskService.findMatchingTasks(playerId, allNewTasks)

		insertAll(playerTasksToInsert)

		if (playerTasksToInsert.all { it.status() == PlayerTaskStatus.IN_PROGRESS }) {
			notificationService.send(NotificationCommand.SaveTasks(playerId))
		} else {
			notificationService.send(NotificationCommand.SilentTasksUpdate(playerId))
		}

		val tasksToGenerate = playerTasksToInsert.filter { it.status() == PlayerTaskStatus.PREPARING }
			.map { it.task()!! }

		generateTasksProducer.send(playerId, tasksToGenerate)
	}

	private fun setStatus(
		playerTasks: Collection<PlayerTask>,
		status: PlayerTaskStatus,
		saveTask: Boolean = false
	) {
		if (playerTasks.isEmpty()) {
			return
		}

		val expectedStatus = when (status) {
			PlayerTaskStatus.IN_PROGRESS -> PlayerTaskStatus.PREPARING
			PlayerTaskStatus.COMPLETED,
			PlayerTaskStatus.SKIPPED -> PlayerTaskStatus.IN_PROGRESS

			else -> throw IllegalArgumentException("Incorrect status=$status")
		}

		val updatedTasks = playerTasks.map {
			require(it.status() == expectedStatus) {
				"PlayerTask=${it.id()} status must be $expectedStatus"
			}

			Immutables.createPlayerTask(it) { p ->
				if (!saveTask && ImmutableObjects.isLoaded(it, PlayerTaskProps.TASK)) {
					val task = it.task()!!
					p.setTask(null)
						.setTaskId(task.id())
				}

				if (ImmutableObjects.isLoaded(it, PlayerTaskProps.PLAYER)) {
					val player = it.player()!!
					p.setPlayer(null)
						.setPlayerId(player.id())
				}

				p.setStatus(status)
			}
		}

		playerTaskRepository.saveEntities(updatedTasks, SaveMode.UPDATE_ONLY)
	}
}
