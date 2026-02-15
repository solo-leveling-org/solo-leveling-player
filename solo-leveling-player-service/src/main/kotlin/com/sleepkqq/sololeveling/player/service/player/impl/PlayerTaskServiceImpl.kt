package com.sleepkqq.sololeveling.player.service.player.impl

import com.sleepkqq.sololeveling.avro.task.SaveTasksOperation
import com.sleepkqq.sololeveling.player.config.properties.PlayerLimitsProperties
import com.sleepkqq.sololeveling.player.config.properties.TasksProperties
import com.sleepkqq.sololeveling.player.event.model.TaskCompletedEvent
import com.sleepkqq.sololeveling.player.exception.AccessDeniedException
import com.sleepkqq.sololeveling.player.kafka.producer.GenerateTasksProducer
import com.sleepkqq.sololeveling.player.kafka.producer.TasksSavedProducer
import com.sleepkqq.sololeveling.player.model.entity.Immutables
import com.sleepkqq.sololeveling.player.model.entity.player.Player
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTask
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTaskFetcher
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTaskProps
import com.sleepkqq.sololeveling.player.model.entity.player.dto.*
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerBalanceTransactionCause
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerTaskStatus
import com.sleepkqq.sololeveling.player.model.entity.task.Task
import com.sleepkqq.sololeveling.player.model.entity.task.dto.GenerateTaskView
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic
import com.sleepkqq.sololeveling.player.model.repository.player.PlayerTaskRepository
import com.sleepkqq.sololeveling.player.service.player.*
import com.sleepkqq.sololeveling.player.service.task.TaskService
import com.sleepkqq.sololeveling.proto.player.RequestPaging
import com.sleepkqq.sololeveling.proto.player.RequestQueryOptions
import org.babyfish.jimmer.ImmutableObjects
import org.babyfish.jimmer.Page
import org.babyfish.jimmer.View
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.*
import kotlin.reflect.KClass

@Service
@EnableConfigurationProperties(PlayerLimitsProperties::class, TasksProperties::class)
class PlayerTaskServiceImpl(
	private val playerTaskRepository: PlayerTaskRepository,
	private val playerBalanceService: PlayerBalanceService,
	private val playerService: PlayerService,
	private val levelService: LevelService,
	private val taskService: TaskService,
	private val generateTasksProducer: GenerateTasksProducer,
	private val playerLimitsProperties: PlayerLimitsProperties,
	private val playerStaminaService: PlayerStaminaService,
	private val tasksProperties: TasksProperties,
	private val eventPublisher: ApplicationEventPublisher,
	private val tasksSavedProducer: TasksSavedProducer
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
	override fun <V : View<PlayerTask>> getActiveTasks(playerId: Long, viewType: KClass<V>): List<V> =
		playerTaskRepository.findByPlayerIdAndStatusIn(playerId, ACTIVE_TASKS_STATUSES, viewType.java)

	@Transactional(readOnly = true)
	override fun getPreparingTasksForRetry(): List<PreparingPlayerTaskView> =
		playerTaskRepository.findPreparingTasksForRetry()

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
		val playerTask = getView(id, SkipPlayerTaskView::class)

		val player = playerTask.player

		if (player.id != playerId) {
			throw AccessDeniedException()
		}

		val consumedStamina = playerStaminaService.consume(
			player.stamina.toEntity(),
			tasksProperties.getSkipCost()
		)
		playerStaminaService.update(consumedStamina)

		setStatus(listOf(playerTask.toEntity()), PlayerTaskStatus.SKIPPED)

		generateTasks(
			playerId = playerId,
			replaceOrders = setOf(playerTask.order),
			operation = SaveTasksOperation.SKIP
		)
	}

	@Transactional
	override fun completeTask(playerId: Long, id: UUID): Pair<PlayerView, PlayerView> {
		val playerTask = getView(id, CompletePlayerTaskView::class)

		val player = playerTask.player
		val task = playerTask.task

		if (player.id != playerId) {
			throw AccessDeniedException()
		}

		val updatedStamina = playerStaminaService.consume(
			player.stamina.toEntity(),
			tasksProperties.getCompleteCost(task.rarity)
		)

		setStatus(listOf(playerTask.toEntity()), PlayerTaskStatus.COMPLETED)

		val updatedBalance = playerBalanceService.deposit(
			player.balance.toEntity(),
			BigDecimal(task.currencyReward),
			PlayerBalanceTransactionCause.TASK_COMPLETION
		)

		val topics = task.topics.map { it.topic }
		val gainedExperiencePlayer = levelService.gainExperience(
			player.toEntity(),
			topics,
			task.experience
		)

		val updatedPlayer = playerService.update(
			Immutables.createPlayer(gainedExperiencePlayer) {
				it.setAgility(player.agility + task.agility)
					.setStrength(player.strength + task.strength)
					.setIntelligence(player.intelligence + task.intelligence)
					.setBalance(updatedBalance)
					.setStamina(updatedStamina)
			}
		)

		generateTasks(playerId, updatedPlayer, setOf(playerTask.order), SaveTasksOperation.COMPLETE)

		eventPublisher.publishEvent(TaskCompletedEvent(playerId, task.rarity))

		return filterPlayerTopics(player.toEntity(), topics) to
				filterPlayerTopics(updatedPlayer, topics)
	}

	private fun filterPlayerTopics(player: Player, topics: Collection<TaskTopic>): PlayerView =
		PlayerView(Immutables.createPlayer(player) {
			it.setTaskTopics(player.taskTopics().filter { t -> t.taskTopic() in topics })
		})

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
		replaceOrders: Set<Int>,
		operation: SaveTasksOperation
	) {
		val resolvedPlayer = player
			?: playerService.getView(playerId, GenerateTasksPlayerView::class).toEntity()

		val activeTasks = getActiveTasks(playerId, GeneratePlayerTaskView::class)

		activeTasks.firstOrNull { it.order in replaceOrders }
			?.let { throw IllegalArgumentException("Incorrect replacement order ${it.order}") }

		val maxTasks = playerLimitsProperties.limits.free.tasks.max
		val additionalTasksNeeded = maxTasks - activeTasks.size

		if (additionalTasksNeeded <= 0) {
			log.warn(
				"Invalid state: activeTasksCount={} exceeds maxTasks={} for playerId={}, skipping generation",
				activeTasks.size, maxTasks, playerId
			)
			return
		}

		val usedOrders = activeTasks.map { it.order }.toSet()

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

		val tasksToGenerate = playerTasksToInsert.filter { it.status() == PlayerTaskStatus.PREPARING }
			.map { GenerateTaskView(it.task()!!) }

		val hasTasksToGenerate = tasksToGenerate.isNotEmpty()
		if (hasTasksToGenerate) {
			generateTasksProducer.send(userId = playerId, tasks = tasksToGenerate, operation = operation)
		}

		tasksSavedProducer.send(
			userId = playerId,
			operation = operation.takeUnless { hasTasksToGenerate }
		)
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
