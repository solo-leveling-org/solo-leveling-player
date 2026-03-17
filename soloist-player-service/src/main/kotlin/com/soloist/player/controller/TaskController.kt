package com.soloist.player.controller

import com.soloist.config.interceptor.UserContextHolder
import com.soloist.player.mapper.ProtoMapper
import com.soloist.player.model.entity.task.enums.ProofType
import com.soloist.player.service.task.TaskProofService
import com.soloist.player.service.task.TaskService
import com.soloist.proto.task.CreateCustomTaskRequest
import com.soloist.proto.task.CreateCustomTaskResponse
import com.soloist.proto.task.GetTaskHistoryRequest
import com.soloist.proto.task.GetTaskHistoryResponse
import com.soloist.proto.task.GetTasksRequest
import com.soloist.proto.task.GetTasksResponse
import com.soloist.proto.task.SubmitTaskProofRequest
import com.soloist.proto.task.SubmitTaskProofResponse
import com.soloist.proto.task.TaskServiceGrpc
import io.grpc.stub.StreamObserver
import org.springframework.grpc.server.service.GrpcService
import java.util.UUID

@GrpcService
class TaskController(
	private val taskService: TaskService,
	private val taskProofService: TaskProofService,
	private val protoMapper: ProtoMapper
) : TaskServiceGrpc.TaskServiceImplBase() {

	override fun getTasks(
		request: GetTasksRequest,
		responseObserver: StreamObserver<GetTasksResponse>
	) {
		val tasks = taskService.getOrInitializeTasks(request.playerId)

		val response = GetTasksResponse.newBuilder()
			.addAllTasks(tasks.map { protoMapper.mapTask(it) })
			.build()

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}

	override fun createCustomTask(
		request: CreateCustomTaskRequest,
		responseObserver: StreamObserver<CreateCustomTaskResponse>
	) {
		val playerId = UserContextHolder.getUserId()!!
		val task = taskService.createCustomTask(playerId, request.name)

		val response = CreateCustomTaskResponse.newBuilder()
			.setIsValid(true)
			.setTask(protoMapper.mapTask(task))
			.build()

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}

	override fun submitTaskProof(
		request: SubmitTaskProofRequest,
		responseObserver: StreamObserver<SubmitTaskProofResponse>
	) {
		val taskId = UUID.fromString(request.taskId)
		val proofType = ProofType.valueOf(request.proofType.name)

		val result = taskProofService.submitProof(
			taskId = taskId,
			proofType = proofType,
			text = request.text.takeIf { it.isNotBlank() },
			telegramFileId = request.telegramFileId.takeIf { it.isNotBlank() },
			secretWord = request.secretWord.takeIf { it.isNotBlank() }
		)

		val response = SubmitTaskProofResponse.newBuilder()
			.setIsApproved(result.isApproved)
			.setGemReward(result.gemReward)
			.setProgress(result.progress)
			.setGoal(result.goal)
			.setProgressIncrement(result.progressIncrement)
			.also { builder -> result.rejectionReason?.let { builder.setRejectionReason(it) } }
			.build()

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}

	override fun getTaskHistory(
		request: GetTaskHistoryRequest,
		responseObserver: StreamObserver<GetTaskHistoryResponse>
	) {
		val paging = request.paging
		val historyPage = taskService.getHistory(
			playerId = request.playerId,
			page = paging.page,
			pageSize = paging.pageSize
		)

		val response = GetTaskHistoryResponse.newBuilder()
			.addAllTasks(historyPage.rows.map { protoMapper.mapTask(it) })
			.setPaging(protoMapper.map(historyPage.totalRowCount, historyPage.totalPageCount, paging.pageSize))
			.build()

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}
}
