package com.sleepkqq.sololeveling.player.service.player.impl

import com.sleepkqq.sololeveling.player.model.entity.Fetchers
import com.sleepkqq.sololeveling.player.model.entity.Immutables
import com.sleepkqq.sololeveling.player.model.entity.player.Player
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTask
import com.sleepkqq.sololeveling.player.model.entity.player.TaskTopicItem
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
import com.sleepkqq.sololeveling.player.service.player.PlayerTaskService
import com.sleepkqq.sololeveling.player.service.task.TaskService
import com.sleepkqq.sololeveling.proto.player.RequestQueryOptions
import org.babyfish.jimmer.Page
import org.babyfish.jimmer.View
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.babyfish.jimmer.sql.fetcher.Fetcher
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.UUID
import kotlin.reflect.KClass

@Service
class PlayerTaskServiceImpl(
	private val playerTaskRepository: PlayerTaskRepository,
	private val playerBalanceService: PlayerBalanceService,
	private val playerService: PlayerService,
	private val levelService: LevelService,
	private val taskService: TaskService,
	private val notificationService: NotificationService
) : PlayerTaskService {

	private val log = LoggerFactory.getLogger(javaClass)

	private companion object {
		val ACTIVE_TASKS_STATUSES = setOf(
			PlayerTaskStatus.PREPARING,
			PlayerTaskStatus.IN_PROGRESS
		)
	}

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
	override fun skipTask(playerId: Long, playerTask: PlayerTask) {
		setStatus(listOf(playerTask), PlayerTaskStatus.SKIPPED)

		generateTasks(playerId, replaceOrders = setOf(playerTask.order()))
	}

	@Transactional
	override fun completeTask(
		playerId: Long,
		playerTask: PlayerTask,
		task: Task
	): Pair<PlayerView, PlayerView> {

		setStatus(listOf(playerTask), PlayerTaskStatus.COMPLETED)

		val playerView = playerService.getView(playerId, PlayerView::class)
		val player = playerView.toEntity()

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
				it.setStrength(player.strength() + task.strength()!!)
				it.setIntelligence(player.intelligence() + task.intelligence()!!)
				it.setBalance(updatedBalance)
			}
		)

		generateTasks(playerId, updatedPlayer, setOf(playerTask.order()))

		return playerView to PlayerView(updatedPlayer)
	}

	@Transactional
	override fun inProgressTasks(tasks: Collection<PlayerTask>) {
		setStatus(tasks, PlayerTaskStatus.IN_PROGRESS)
	}

	@Transactional(readOnly = true)
	override fun <V : View<PlayerTask>> searchView(
		playerId: Long,
		options: RequestQueryOptions,
		viewType: KClass<V>
	): Page<V> = playerTaskRepository.searchView(playerId, options, viewType.java)

	@Transactional
	override fun generateTasks(
		playerId: Long,
		player: Player?,
		replaceOrders: Set<Int>
	) {
		val resolvedPlayer = player ?: playerService.get(
			playerId,
			Fetchers.PLAYER_FETCHER.maxTasks()
				.taskTopics(
					Fetchers.PLAYER_TASK_TOPIC_FETCHER
						.taskTopic()
						.active()
						.level(
							Fetchers.LEVEL_FETCHER.level()
						)
				)
		)

		val activeTasks = getActiveTasks(playerId, Fetchers.PLAYER_TASK_FETCHER.order())
		val maxTasks = resolvedPlayer.maxTasks()
		val additionalTasksNeeded = maxTasks - activeTasks.size

		if (additionalTasksNeeded < 0) {
			log.warn(
				"Invalid state: activeTasksCount={} exceeds maxTasks={} for playerId={}, skipping generation",
				activeTasks, maxTasks, playerId
			)
			return
		}

		val usedOrders = activeTasks.map { it.order() }.toSet()

		val additionalOrders = if (additionalTasksNeeded > 0) {
			(0 until maxTasks)
				.filterNot { it in usedOrders }
				.take(additionalTasksNeeded)
		} else {
			emptyList()
		}

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
		}

		val tasksToGenerate = playerTasksToInsert.filter { it.status() == PlayerTaskStatus.PREPARING }
			.map { it.task()!! }

		taskService.generateTasks(playerId, tasksToGenerate)
	}

	private fun setStatus(playerTasks: Collection<PlayerTask>, status: PlayerTaskStatus) {
		playerTaskRepository.saveEntities(
			playerTasks.map {
				Immutables.createPlayerTask(it) { p ->
					p.setStatus(status)
				}
			},
			SaveMode.UPDATE_ONLY
		)
	}
}
