package com.soloist.player.controller

import com.google.protobuf.Empty
import com.soloist.avro.task.SaveTasksOperation
import com.soloist.config.interceptor.UserContextHolder
import com.soloist.jimmer.enums.EnumLocalizer
import com.soloist.player.mapper.ProtoMapper
import com.soloist.player.model.entity.task.dto.DailyTaskView
import com.soloist.player.model.entity.task.dto.PlayerTaskView
import com.soloist.player.model.repository.task.PlayerTaskRepository
import com.soloist.player.service.i18n.LocalizationCode
import com.soloist.player.service.player.DailyTaskService
import com.soloist.player.service.task.PlayerTaskService
import com.soloist.player.service.task.TaskService
import com.soloist.proto.task.*
import io.grpc.stub.StreamObserver
import org.springframework.grpc.server.service.GrpcService
import java.util.*

@GrpcService
class TaskController(
	private val playerTaskService: PlayerTaskService,
	private val protoMapper: ProtoMapper,
	private val taskService: TaskService,
	private val enumLocalizer: EnumLocalizer,
	private val dailyTaskService: DailyTaskService
) : TaskServiceGrpc.TaskServiceImplBase() {

	override fun getActiveTasks(
		request: GetActiveTasksRequest,
		responseObserver: StreamObserver<GetActiveTasksResponse>
	) {
		val playerId = request.playerId

		val activeTasks = playerTaskService.getActiveTasks(playerId, PlayerTaskView::class)
			.map(protoMapper::map)

		val isFirstTime = activeTasks.isEmpty()

		val response = GetActiveTasksResponse.newBuilder()
			.addAllTasks(activeTasks)
			.setIsFirstTime(isFirstTime)
			.build()

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}

	override fun generateTasks(
		request: Empty,
		responseObserver: StreamObserver<Empty>
	) {
		playerTaskService.generateTasks(
			playerId = UserContextHolder.getUserId()!!,
			operation = SaveTasksOperation.INITIALIZE
		)

		responseObserver.onNext(Empty.newBuilder().build())
		responseObserver.onCompleted()
	}

	override fun completeTask(
		request: CompleteTaskRequest,
		responseObserver: StreamObserver<CompleteTaskResponse>
	) {
		val playerStates = playerTaskService.completeTask(
			UserContextHolder.getUserId()!!,
			UUID.fromString(request.playerTaskId)
		)

		val response = CompleteTaskResponse.newBuilder()
			.setPlayerBefore(protoMapper.map(playerStates.first))
			.setPlayerAfter(protoMapper.map(playerStates.second))
			.build()

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}

	override fun skipTask(
		request: SkipTaskRequest,
		responseObserver: StreamObserver<Empty>
	) {
		playerTaskService.skipTask(
			UserContextHolder.getUserId()!!,
			UUID.fromString(request.playerTaskId)
		)

		responseObserver.onNext(Empty.newBuilder().build())
		responseObserver.onCompleted()
	}

	override fun searchClosedTasks(
		request: SearchClosedTasksRequest,
		responseObserver: StreamObserver<SearchClosedTasksResponse>
	) {
		val tasksPage = playerTaskService.searchView(
			UserContextHolder.getUserId()!!,
			request.options,
			request.paging,
			PlayerTaskView::class
		)
		val response = protoMapper.mapTasks(
			tasksPage,
			request.paging.page,
			enumLocalizer.localize(
				LocalizationCode.TABLES_PLAYER_TASKS,
				PlayerTaskRepository.FIELD_ENUM_TYPES,
				PlayerTaskRepository.ENUM_TYPE_PREDICATES
			)
		)

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}

	override fun deprecateTasksByTopic(
		request: DeprecateTasksByTopicRequest,
		responseObserver: StreamObserver<DeprecateTasksByTopicResponse>
	) {
		val affectedRows = taskService.deprecateByTopic(protoMapper.map(request.taskTopic))

		val response = DeprecateTasksByTopicResponse.newBuilder()
			.setAffectedRows(affectedRows)
			.build()

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}

	override fun deprecateAllTasks(
		request: Empty,
		responseObserver: StreamObserver<DeprecateAllTasksResponse>
	) {
		val affectedRows = taskService.deprecateAll()

		val response = DeprecateAllTasksResponse.newBuilder()
			.setAffectedRows(affectedRows)
			.build()

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}

	override fun getDailyTasks(
		request: GetDailyTasksRequest,
		responseObserver: StreamObserver<GetDailyTasksResponse>
	) {
		val dailyTasks = dailyTaskService.findView(
			request.playerId,
			DailyTaskView::class
		)

		val sortedMappedTasks = dailyTasks.sortedBy { it.type.ordinal }
			.map { protoMapper.map(it) }

		val response = GetDailyTasksResponse.newBuilder()
			.addAllTasks(sortedMappedTasks)
			.build()

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}
}
