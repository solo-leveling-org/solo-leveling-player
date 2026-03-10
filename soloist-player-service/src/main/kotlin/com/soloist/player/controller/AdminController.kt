package com.soloist.player.controller

import com.google.protobuf.Empty
import com.soloist.player.mapper.ProtoMapper
import com.soloist.player.model.entity.gacha.dto.GachaMachineView
import com.soloist.player.model.entity.gear.dto.GearItemView
import com.soloist.player.model.entity.user.dto.LocaleUserView
import com.soloist.player.service.gacha.GachaMachineService
import com.soloist.player.service.gacha.GachaService
import com.soloist.player.service.gear.GearItemService
import com.soloist.player.service.player.PlayerService
import com.soloist.player.service.task.TaskService
import com.soloist.player.service.user.UserService
import com.soloist.proto.admin.AdminServiceGrpc
import com.soloist.proto.admin.DeprecateTasksByTopicRequest
import com.soloist.proto.admin.DeprecateTasksResponse
import com.soloist.proto.admin.ResetPlayerRequest
import com.soloist.proto.gacha.AddItemToMachineRequest
import com.soloist.proto.gacha.CreateGachaMachineRequest
import com.soloist.proto.gacha.CreateGachaMachineResponse
import com.soloist.proto.gacha.CreateGearItemRequest
import com.soloist.proto.gacha.CreateGearItemResponse
import com.soloist.proto.gacha.ListGachaMachinesResponse
import com.soloist.proto.gacha.ListGearItemsRequest
import com.soloist.proto.gacha.ListGearItemsResponse
import com.soloist.proto.gacha.RemoveItemFromMachineRequest
import com.soloist.proto.user.GetUsersRequest
import com.soloist.proto.user.GetUsersResponse
import com.soloist.proto.user.GetUsersStatsResponse
import io.grpc.stub.StreamObserver
import org.springframework.grpc.server.service.GrpcService
import java.util.UUID

@GrpcService
class AdminController(
	private val protoMapper: ProtoMapper,
	private val playerService: PlayerService,
	private val taskService: TaskService,
	private val userService: UserService,
	private val gearItemService: GearItemService,
	private val gachaMachineService: GachaMachineService,
	private val gachaService: GachaService
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

	// ── Gear items ────────────────────────────────────────

	override fun createGearItem(
		request: CreateGearItemRequest,
		responseObserver: StreamObserver<CreateGearItemResponse>
	) {
		val input = protoMapper.map(request.gearItem)
		val gearItem = gearItemService.create(
			input = input,
			imageFileId = request.imageFileId
		)

		val response = CreateGearItemResponse.newBuilder()
			.setGearItem(protoMapper.map(GearItemView(gearItem)))
			.build()

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}

	override fun listGearItems(
		request: ListGearItemsRequest,
		responseObserver: StreamObserver<ListGearItemsResponse>
	) {
		val page = gearItemService.find(
			pageIndex = request.paging.page,
			pageSize = request.paging.pageSize
		)

		val response = ListGearItemsResponse.newBuilder()
			.addAllItems(page.rows.map { protoMapper.map(it) })
			.setPaging(
				protoMapper.map(
					page.totalRowCount,
					page.totalPageCount,
					request.paging.pageSize
				)
			)
			.build()

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}

	// ── Gacha machines ────────────────────────────────────

	override fun createGachaMachine(
		request: CreateGachaMachineRequest,
		responseObserver: StreamObserver<CreateGachaMachineResponse>
	) {
		val input = protoMapper.map(request.machine)
		val machine = gachaMachineService.create(
			input = input,
			imageFileId = request.imageFileId
		)

		val response = CreateGachaMachineResponse.newBuilder()
			.setMachine(protoMapper.map(GachaMachineView(machine)))
			.build()

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}

	override fun listGachaMachines(
		request: Empty,
		responseObserver: StreamObserver<ListGachaMachinesResponse>
	) {
		val machines = gachaService.getActiveMachines()
			.map { protoMapper.map(it) }

		val response = ListGachaMachinesResponse.newBuilder()
			.addAllMachines(machines)
			.build()

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}

	override fun addItemToMachine(
		request: AddItemToMachineRequest,
		responseObserver: StreamObserver<Empty>
	) {
		gachaMachineService.addItem(
			machineId = UUID.fromString(request.machineId),
			gearItemId = UUID.fromString(request.gearItemId),
			weight = request.weight
		)

		responseObserver.onNext(Empty.getDefaultInstance())
		responseObserver.onCompleted()
	}

	override fun removeItemFromMachine(
		request: RemoveItemFromMachineRequest,
		responseObserver: StreamObserver<Empty>
	) {
		gachaMachineService.removeItem(
			machineId = UUID.fromString(request.machineId),
			gearItemId = UUID.fromString(request.gearItemId)
		)

		responseObserver.onNext(Empty.getDefaultInstance())
		responseObserver.onCompleted()
	}
}
