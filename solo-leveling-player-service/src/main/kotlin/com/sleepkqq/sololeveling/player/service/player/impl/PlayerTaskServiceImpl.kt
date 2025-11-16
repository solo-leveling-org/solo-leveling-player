package com.sleepkqq.sololeveling.player.service.player.impl

import com.sleepkqq.sololeveling.avro.task.GenerateTask
import com.sleepkqq.sololeveling.avro.task.GenerateTasksEvent
import com.sleepkqq.sololeveling.player.kafka.producer.GenerateTasksProducer
import com.sleepkqq.sololeveling.player.mapper.AvroMapper
import com.sleepkqq.sololeveling.player.model.entity.Fetchers
import com.sleepkqq.sololeveling.player.model.entity.Immutables
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTask
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTaskTopic
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
import com.sleepkqq.sololeveling.player.service.task.DefineTaskRarityService
import com.sleepkqq.sololeveling.player.service.task.DefineTaskTopicService
import com.sleepkqq.sololeveling.proto.player.RequestQueryOptions
import org.babyfish.jimmer.Page
import org.babyfish.jimmer.View
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.UUID
import kotlin.reflect.KClass

@Service
class PlayerTaskServiceImpl(
	private val playerTaskRepository: PlayerTaskRepository,
	private val generateTasksProducer: GenerateTasksProducer,
	private val playerBalanceService: PlayerBalanceService,
	private val playerService: PlayerService,
	private val levelService: LevelService,
	private val notificationService: NotificationService,
	private val defineTaskTopicService: DefineTaskTopicService,
	private val defineTaskRarityService: DefineTaskRarityService,
	private val avroMapper: AvroMapper
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
	override fun insert(playerTask: PlayerTask): PlayerTask =
		playerTaskRepository.save(playerTask, SaveMode.INSERT_ONLY)

	@Transactional
	override fun update(playerTask: PlayerTask): PlayerTask =
		playerTaskRepository.save(playerTask, SaveMode.UPDATE_ONLY)

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
	override fun getActiveTasksCount(playerId: Long): Long =
		playerTaskRepository.countByPlayerIdAndStatusIn(playerId, ACTIVE_TASKS_STATUSES)

	override fun initialize(playerId: Long, order: Int): PlayerTask = Immutables.createPlayerTask {
		it.setId(UUID.randomUUID())
		it.setStatus(PlayerTaskStatus.PREPARING)
		it.setOrder(order)
		it.setPlayerId(playerId)
		it.setTask(
			Immutables.createTask { t ->
				t.setId(UUID.randomUUID())
				t.setVersion(0)
			}
		)
	}

	@Transactional
	override fun skipTask(playerId: Long, playerTask: PlayerTask) {
		setStatus(listOf(playerTask), PlayerTaskStatus.SKIPPED)

		generateTasks(playerId, setOf(playerTask.order()))
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

		generateTasks(playerId, setOf(playerTask.order()))

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

	// todo: refactor
	@Transactional
	override fun generateTasks(playerId: Long, replaceOrders: Set<Int>) {
		val player = playerService.get(
			playerId,
			Fetchers.PLAYER_FETCHER.maxTasks()
				.taskTopics(
					Fetchers.PLAYER_TASK_TOPIC_FETCHER
						.taskTopic()
						.active()
						.level(
							Fetchers.LEVEL_FETCHER
								.level()
						)
				)
				.tasks(
					Fetchers.PLAYER_TASK_FETCHER
						.order()
				)
		)

		val activeTasksCount = getActiveTasksCount(playerId)
		val maxTasks = player.maxTasks()

		// Генерируем задачи для замены (если указаны)
		val replaceTasks = replaceOrders.map {
			initialize(playerId, it)
		}

		// Вычисляем сколько еще нужно задач для достижения maxTasks
		val additionalTasksNeeded = maxTasks - activeTasksCount

		if (additionalTasksNeeded < 0) {
			log.warn(
				"Invalid state: tasksAfterReplace={} exceeds maxTasks={} for playerId={}, skipping generation",
				activeTasksCount, maxTasks, playerId
			)
			return
		}

		val usedOrders = player.tasks()
			.map { it.order() }

		// Генерируем дополнительные задачи для заполнения до maxTasks
		val additionalTasks = if (additionalTasksNeeded > 0) {
			(0 until maxTasks)
				.filterNot { it in usedOrders }
				.take(additionalTasksNeeded.toInt())
				.map { initialize(playerId, it) }
		} else {
			emptyList()
		}

		val allNewTasks = replaceTasks + additionalTasks

		if (allNewTasks.isEmpty()) {
			log.warn("No tasks generated for playerId={}, skipping", playerId)
			return
		}

		log.info(
			"Generated {} tasks for player {} ({} replace, {} new)",
			allNewTasks.size, playerId, replaceTasks.size, additionalTasks.size
		)

		insertAll(allNewTasks)

		val generateTasks = allNewTasks.map {
			generateTask(it.task(), player.taskTopics())
		}

		val event = GenerateTasksEvent.newBuilder()
			.setTxId(UUID.randomUUID().toString())
			.setPlayerId(playerId)
			.setInputs(generateTasks)
			.build()

		generateTasksProducer.send(event)

		notificationService.send(NotificationCommand.SilentTasksUpdate(playerId))
	}

	private fun generateTask(task: Task, playerTaskTopics: List<PlayerTaskTopic>): GenerateTask {
		val playerTaskTopicsMap = playerTaskTopics
			.filter(PlayerTaskTopic::isActive)
			.associateBy(PlayerTaskTopic::taskTopic)

		val definedTopics = defineTaskTopicService.define(playerTaskTopicsMap.keys)
		val chosenTopics = definedTopics.map(playerTaskTopicsMap::getValue)

		val rarity = defineTaskRarityService.define(chosenTopics)

		return avroMapper.map(task, definedTopics, rarity)
	}

	private fun setStatus(
		playerTasks: Collection<PlayerTask>,
		status: PlayerTaskStatus
	) {
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
