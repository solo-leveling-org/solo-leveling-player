package com.soloist.player.service.task.impl

import com.soloist.player.exception.ModelNotFoundException
import com.soloist.player.model.entity.Immutables
import com.soloist.player.model.entity.balance.dto.BalanceView
import com.soloist.player.model.entity.balance.enums.BalanceTransactionCause
import com.soloist.player.model.entity.player.enums.CurrencyCode
import com.soloist.player.model.entity.task.Task
import com.soloist.player.model.entity.task.enums.ProofType
import com.soloist.player.model.entity.task.enums.TaskType
import com.soloist.player.model.repository.task.TaskRepository
import com.soloist.player.service.balance.BalanceService
import com.soloist.player.service.notification.NotificationPublisher
import com.soloist.player.service.task.ProofResult
import com.soloist.player.service.task.TaskProofService
import com.soloist.proto.agent.AgentServiceGrpc.AgentServiceBlockingStub
import com.soloist.proto.agent.VerifyTaskProofRequest
import com.soloist.proto.common.ProofType as ProtoProofType
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.UUID

@Service
class TaskProofServiceImpl(
	private val taskRepository: TaskRepository,
	private val balanceService: BalanceService,
	private val notificationPublisher: NotificationPublisher,
	private val agentStub: AgentServiceBlockingStub
) : TaskProofService {

	private val log = LoggerFactory.getLogger(javaClass)

	@Transactional
	override fun submitProof(
		taskId: UUID,
		proofType: ProofType,
		text: String?,
		telegramFileId: String?,
		secretWord: String?
	): ProofResult {
		val task = taskRepository.findById(taskId).orElseThrow {
			ModelNotFoundException(Task::class, taskId)
		}

		val taskTitle = task.name() ?: task.type().name
		val taskDescription = if (task.type() == TaskType.CUSTOM) {
			"Custom task: ${task.name()}"
		} else {
			"Default task: ${task.type().name}, goal=${task.goal()}"
		}

		val verifyResponse = callAgentVerify(
			taskTitle = taskTitle,
			taskDescription = taskDescription,
			secretWord = secretWord.orEmpty(),
			proofType = proofType,
			text = text,
			telegramFileId = telegramFileId,
			goal = task.goal()
		)

		if (!verifyResponse.isApproved) {
			log.info("Task proof rejected for taskId={}, reason={}", task.id(), verifyResponse.rejectionReason)
			return ProofResult(
				isApproved = false,
				rejectionReason = verifyResponse.rejectionReason,
				gemReward = 0,
				progress = task.progress(),
				goal = task.goal(),
				progressIncrement = 0
			)
		}

		val playerId = task.player().id()
		log.info(
			"Proof approved for taskId={}: currentProgress={}, aiProgress={}, goal={}",
			task.id(), task.progress(), verifyResponse.progress, task.goal()
		)
		val increment = verifyResponse.progress
		val newProgress = (task.progress() + increment).coerceAtMost(task.goal())
		val isCompleted = newProgress >= task.goal()

		taskRepository.save(
			Immutables.createTask(task) { it.setCompleted(isCompleted).setProgress(newProgress) },
			SaveMode.UPDATE_ONLY
		)

		val gemReward = if (isCompleted) task.gemReward() else 0
		if (isCompleted) {
			deposit(playerId, gemReward)
			notificationPublisher.sendTaskCompleted(playerId, taskTitle, gemReward)
			log.info("Task completed, taskId={}, playerId={}, gemReward={}", task.id(), playerId, gemReward)
		} else {
			log.info("Task progress updated, taskId={}, playerId={}, progress={}/{}", task.id(), playerId, newProgress, task.goal())
		}

		return ProofResult(
			isApproved = true,
			rejectionReason = null,
			gemReward = gemReward,
			progress = newProgress,
			goal = task.goal(),
			progressIncrement = increment
		)
	}

	private fun callAgentVerify(
		taskTitle: String,
		taskDescription: String,
		secretWord: String,
		proofType: ProofType,
		text: String?,
		telegramFileId: String?,
		goal: Int
	): com.soloist.proto.agent.VerifyTaskProofResponse {
		val requestBuilder = VerifyTaskProofRequest.newBuilder()
			.setTaskTitle(taskTitle)
			.setTaskDescription(taskDescription)
			.setSecretWord(secretWord)
			.setProofType(ProtoProofType.valueOf(proofType.name))
			.setGoal(goal)

		if (!text.isNullOrBlank()) requestBuilder.setText(text)
		if (!telegramFileId.isNullOrBlank()) requestBuilder.setTelegramFileId(telegramFileId)

		return agentStub.verifyTaskProof(requestBuilder.build())
	}

	private fun deposit(playerId: Long, gemReward: Int) {
		val gemBalanceView = balanceService.findView(playerId, BalanceView::class)
			?: throw IllegalStateException("GEM balance not found for playerId=$playerId")

		balanceService.deposit(
			balance = gemBalanceView.toEntity(),
			amount = BigDecimal(gemReward),
			cause = BalanceTransactionCause.TASK_COMPLETION,
			currencyCode = CurrencyCode.GEM
		)
	}
}
