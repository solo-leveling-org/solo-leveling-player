package com.soloist.player.service.task.impl

import com.soloist.player.model.entity.Immutables
import com.soloist.player.model.entity.task.PlayerTask
import com.soloist.player.model.entity.task.PlayerTaskTopic
import com.soloist.player.model.entity.task.enums.PlayerTaskStatus
import com.soloist.player.model.entity.task.Task
import com.soloist.player.model.entity.task.dto.VectorizeTaskView
import com.soloist.player.model.entity.task.enums.TaskTopic
import com.soloist.player.model.repository.task.TaskRepository
import com.soloist.player.service.ai.TaskVectorService
import com.soloist.player.service.task.DefineTaskRarityService
import com.soloist.player.service.task.DefineTaskTopicService
import com.soloist.player.service.task.TaskService
import com.soloist.proto.common.RequestPaging
import org.babyfish.jimmer.Page
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class TaskServiceImpl(
	private val taskRepository: TaskRepository,
	private val defineTaskTopicService: DefineTaskTopicService,
	private val defineTaskRarityService: DefineTaskRarityService,
	private val taskVectorService: TaskVectorService
) : TaskService {

	private val log = LoggerFactory.getLogger(javaClass)

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

	@Transactional(readOnly = true)
	override fun findMatchingTasks(playerId: Long, playerTasks: List<PlayerTask>): List<PlayerTask> {

		val updatedTasks = when (playerTasks.size) {
			0 -> return emptyList()

			1 -> {
				val playerTask = playerTasks.first()
				val newTaskId = taskRepository.findMatchingTask(playerId, playerTask.task())
				listOf(updateTaskOrKeep(playerTask, newTaskId))
			}

			else -> {
				val matchedTasksMap = taskRepository.findMatchingTasks(playerId, playerTasks)
				playerTasks.map { updateTaskOrKeep(it, matchedTasksMap[it.id()]) }
			}
		}

		return updatedTasks
	}

	private fun updateTaskOrKeep(playerTask: PlayerTask, newTaskId: UUID?): PlayerTask =
		newTaskId?.let {
			Immutables.createPlayerTask(playerTask) {
				it.setTask(null)
					.setTaskId(newTaskId)
					.setStatus(PlayerTaskStatus.IN_PROGRESS)
			}
		} ?: playerTask

	override fun initialize(playerTaskTopics: List<PlayerTaskTopic>): Task {

		val playerTaskTopicsMap = playerTaskTopics
			.filter(PlayerTaskTopic::active)
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

	@Transactional
	override fun deprecateAll(): Int {
		val deletedVectorTasksCount = taskVectorService.deleteAll()
		val deprecatedTasksCount = taskRepository.deprecateAll()

		log.info(
			"Deleted vector tasks: {}, deprecated tasks: {}",
			deletedVectorTasksCount, deprecatedTasksCount
		)

		return deprecatedTasksCount
	}

	@Transactional
	override fun deprecateByTopic(topic: TaskTopic): Int {
		taskVectorService.delete(topic)
		return taskRepository.deprecateByTopic(topic)
	}

	@Transactional(readOnly = true)
	override fun findToVectorize(page: Int, pageSize: Int): Page<VectorizeTaskView> =
		taskRepository.findToVectorize(
			RequestPaging.newBuilder().setPage(page).setPageSize(pageSize).build()
		)
}
