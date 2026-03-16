package com.soloist.player.controller

import com.google.protobuf.Empty
import com.soloist.player.mapper.ProtoMapper
import com.soloist.player.model.entity.user.dto.LocaleUserView
import com.soloist.player.service.player.PlayerService
import com.soloist.player.service.task.TaskService
import com.soloist.player.service.user.UserService
import com.soloist.proto.admin.*
import io.grpc.stub.StreamObserver
import org.springframework.grpc.server.service.GrpcService

@GrpcService
class AdminController(
	private val protoMapper: ProtoMapper,
	private val playerService: PlayerService,
	private val taskService: TaskService,
	private val userService: UserService
) : AdminServiceGrpc.AdminServiceImplBase() {

	// ── Player ────────────────────────────────────────────

	override fun resetPlayer(
		request: ResetPlayerRequest,
		responseObserver: StreamObserver<Empty>
	) {
		playerService.reset(request.playerId)

		responseObserver.onNext(Empty.getDefaultInstance())
		responseObserver.onCompleted()
	}

	// ── Tasks ─────────────────────────────────────────────

	override fun deprecateAllTasks(
		request: Empty,
		responseObserver: StreamObserver<DeprecateTasksResponse>
	) {
		val affectedRows = taskService.deprecateAll()

		val response = DeprecateTasksResponse.newBuilder()
			.setAffectedRows(affectedRows)
			.build()

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}

	override fun deprecateTasksByTopic(
		request: DeprecateTasksByTopicRequest,
		responseObserver: StreamObserver<DeprecateTasksResponse>
	) {
		val affectedRows = taskService.deprecateByTopic(protoMapper.map(request.taskTopic))

		val response = DeprecateTasksResponse.newBuilder()
			.setAffectedRows(affectedRows)
			.build()

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}

	// ── Users ─────────────────────────────────────────────

	override fun getUsersStats(
		request: Empty,
		responseObserver: StreamObserver<GetUsersStatsResponse>
	) {
		val usersStats = userService.getUsersStats()
		val response = protoMapper.map(usersStats)

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}

	override fun getUsers(
		request: GetUsersRequest,
		responseObserver: StreamObserver<GetUsersResponse>
	) {
		val usersPage = userService.getUsers(request.paging, LocaleUserView::class)
		val response = protoMapper.mapLocaleUsers(usersPage, request.paging.page, request.paging.pageSize)

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}
}
