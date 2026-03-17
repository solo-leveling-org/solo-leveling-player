package com.soloist.player.service.task

import com.soloist.player.model.entity.task.enums.ProofType
import java.util.UUID

interface TaskProofService {

	fun submitProof(
		taskId: UUID,
		proofType: ProofType,
		text: String?,
		telegramFileId: String?,
		secretWord: String?
	): ProofResult
}

data class ProofResult(
	val isApproved: Boolean,
	val rejectionReason: String?,
	val gemReward: Int,
	val progress: Int,
	val goal: Int,
	val progressIncrement: Int
)
