package com.sleepkqq.sololeveling.player.service.task.impl

import com.sleepkqq.sololeveling.player.exception.ModelNotFoundException
import com.sleepkqq.sololeveling.player.kafka.producer.GenerateTasksProducer
import com.sleepkqq.sololeveling.player.model.entity.Immutables
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTaskTopic
import com.sleepkqq.sololeveling.player.model.entity.task.Task
import com.sleepkqq.sololeveling.player.model.repository.task.TaskRepository
import com.sleepkqq.sololeveling.player.service.notification.NotificationCommand
import com.sleepkqq.sololeveling.player.service.notification.NotificationService
import com.sleepkqq.sololeveling.player.service.task.DefineTaskRarityService
import com.sleepkqq.sololeveling.player.service.task.DefineTaskTopicService
import com.sleepkqq.sololeveling.player.service.task.TaskService
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class TaskServiceImpl(
	private val taskRepository: TaskRepository,
	private val defineTaskTopicService: DefineTaskTopicService,
	private val defineTaskRarityService: DefineTaskRarityService,
	private val generateTasksProducer: GenerateTasksProducer,
	private val notificationService: NotificationService,
) : TaskService {

	@Transactional(readOnly = true)
	override fun get(id: UUID): Task = find(id)
		?: throw ModelNotFoundException(Task::class, id)

	@Transactional(readOnly = true)
	override fun find(id: UUID): Task? = taskRepository.findNullable(id)

	@Transactional
	override fun updateAll(tasks: Collection<Task>) {
		taskRepository.saveEntities(tasks, SaveMode.UPDATE_ONLY)
	}

	@Transactional
	override fun insert(task: Task): Task =
		taskRepository.save(task, SaveMode.INSERT_ONLY)

	@Transactional
	override fun update(task: Task): Task =
		taskRepository.save(task, SaveMode.UPDATE_ONLY)

	override fun generateTasks(playerId: Long, tasks: List<Task>) {
		generateTasksProducer.send(playerId, tasks)

		notificationService.send(NotificationCommand.SilentTasksUpdate(playerId))
	}

	override fun initialize(playerTaskTopics: List<PlayerTaskTopic>): Task {

		val playerTaskTopicsMap = playerTaskTopics
			.filter(PlayerTaskTopic::isActive)
			.associateBy(PlayerTaskTopic::taskTopic)

		val definedTopics = defineTaskTopicService.define(playerTaskTopicsMap.keys)
		val chosenTopics = definedTopics.map(playerTaskTopicsMap::getValue)

		val rarity = defineTaskRarityService.define(chosenTopics)

		return Immutables.createTask {
			it.setId(UUID.randomUUID())
			it.setVersion(0)
			it.setRarity(rarity)
			definedTopics.forEach { topic ->
				it.addIntoTopics { topicItem ->
					topicItem.setId(UUID.randomUUID())
					topicItem.setTopic(topic)
				}
			}
		}
	}
}
