package com.soloist.player.service.task.impl

import com.soloist.player.exception.CustomTaskValidationException
import com.soloist.player.model.entity.Immutables
import com.soloist.player.model.entity.task.Task
import com.soloist.player.model.entity.task.enums.ProofType
import com.soloist.player.model.entity.task.enums.TaskType
import com.soloist.player.model.repository.task.TaskRepository
import com.soloist.player.service.task.TaskService
import com.soloist.proto.agent.AgentServiceGrpc.AgentServiceBlockingStub
import com.soloist.proto.agent.ValidateCustomTaskRequest
import org.babyfish.jimmer.Page
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.UUID

@Service
class TaskServiceImpl(
	private val taskRepository: TaskRepository,
	private val agentStub: AgentServiceBlockingStub
) : TaskService {

	private val log = LoggerFactory.getLogger(javaClass)

	private companion object {
		val DEFAULT_GOALS = mapOf(
			TaskType.STEPS to 5000,
			TaskType.PUSH_UPS to 30,
			TaskType.SQUATS to 30
		)
		val DEFAULT_GEM_REWARDS = mapOf(
			TaskType.STEPS to 10,
			TaskType.PUSH_UPS to 15,
			TaskType.SQUATS to 15
		)
		val DEFAULT_PROOF_TYPES = mapOf(
			TaskType.STEPS to ProofType.PHOTO,
			TaskType.PUSH_UPS to ProofType.VIDEO,
			TaskType.SQUATS to ProofType.VIDEO
		)
	}

	@Transactional
	override fun getOrInitializeTasks(playerId: Long): List<Task> {
		val today = LocalDate.now(ZoneOffset.UTC)
		val existing = taskRepository.findByPlayerIdAndDay(playerId, today)
		val existingTypes = existing.filter { it.type() != TaskType.CUSTOM }.map { it.type() }.toSet()
		val missingDefaults = TaskType.entries.filter { it != TaskType.CUSTOM && it !in existingTypes }

		if (missingDefaults.isNotEmpty()) {
			log.info("Initializing {} default tasks for playerId={}, day={}", missingDefaults.size, playerId, today)
			val newTasks = missingDefaults.map { type -> createDefaultTask(playerId, type, today) }
			taskRepository.saveAll(newTasks, SaveMode.INSERT_ONLY)
		}

		return taskRepository.findByPlayerIdAndDay(playerId, today)
	}

	@Transactional
	override fun createCustomTask(playerId: Long, name: String): Task {
		log.info("Validating custom task for playerId={}, name={}", playerId, name)

		val validationRequest = ValidateCustomTaskRequest.newBuilder()
			.setName(name)
			.build()

		val validationResponse = agentStub.validateCustomTask(validationRequest)

		if (!validationResponse.isValid) {
			log.info("Custom task rejected for playerId={}, reason={}", playerId, validationResponse.rejectionReason)
			throw CustomTaskValidationException(validationResponse.rejectionReason)
		}

		val proofType = ProofType.valueOf(validationResponse.suggestedProofType.name)
		val today = LocalDate.now(ZoneOffset.UTC)

		val task = Immutables.createTask {
			it.setId(UUID.randomUUID())
				.setType(TaskType.CUSTOM)
				.setName(name)
				.setGoal(1)
				.setProgress(0)
				.setGemReward(validationResponse.suggestedGemReward)
				.setCompleted(false)
				.setProofType(proofType)
				.setDay(today)
				.setPlayerId(playerId)
		}

		return taskRepository.save(task, SaveMode.INSERT_ONLY)
	}

	@Transactional
	override fun initializeDailyTasksForAllPlayers(): Int {
		val today = LocalDate.now(ZoneOffset.UTC)
		val yesterday = today.minusDays(1)

		val playerIds = taskRepository.findDistinctPlayerIdsByDay(yesterday)
		log.info("Found {} players with tasks from yesterday ({})", playerIds.size, yesterday)

		var count = 0
		for (playerId in playerIds) {
			val existing = taskRepository.findByPlayerIdAndDay(playerId, today)
			val existingTypes = existing.filter { it.type() != TaskType.CUSTOM }.map { it.type() }.toSet()
			val missingDefaults = TaskType.entries.filter { it != TaskType.CUSTOM && it !in existingTypes }

			if (missingDefaults.isNotEmpty()) {
				val newTasks = missingDefaults.map { type -> createDefaultTask(playerId, type, today) }
				taskRepository.saveAll(newTasks, SaveMode.INSERT_ONLY)
				count++
			}
		}

		return count
	}

	@Transactional(readOnly = true)
	override fun getHistory(playerId: Long, page: Int, pageSize: Int): Page<Task> {
		val today = LocalDate.now(ZoneOffset.UTC)
		val rows = taskRepository.findCompletedHistory(playerId, today, page, pageSize)
		val totalRowCount = taskRepository.countCompletedHistory(playerId, today)
		val totalPageCount = if (pageSize > 0) (totalRowCount + pageSize - 1) / pageSize else 0L

		return Page(rows, totalRowCount, totalPageCount)
	}

	private fun createDefaultTask(playerId: Long, type: TaskType, day: LocalDate): Task {
		val proofType = DEFAULT_PROOF_TYPES[type]!!

		return Immutables.createTask {
			it.setId(UUID.randomUUID())
				.setType(type)
				.setName(null)
				.setGoal(DEFAULT_GOALS[type]!!)
				.setProgress(0)
				.setGemReward(DEFAULT_GEM_REWARDS[type]!!)
				.setCompleted(false)
				.setProofType(proofType)
				.setDay(day)
				.setPlayerId(playerId)
		}
	}
}
