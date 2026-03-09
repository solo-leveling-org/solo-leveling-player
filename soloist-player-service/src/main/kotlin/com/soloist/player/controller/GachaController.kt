package com.soloist.player.controller

import com.google.protobuf.Empty
import com.soloist.player.mapper.ProtoMapper
import com.soloist.player.model.entity.gacha.dto.GachaMachineView
import com.soloist.player.model.entity.gear.dto.GearItemView
import com.soloist.player.service.gacha.GachaService
import com.soloist.player.service.gacha.GachaMachineService
import com.soloist.player.service.gear.GearItemService
import com.soloist.player.service.gear.PlayerInventoryService
import com.soloist.proto.gacha.AddItemToMachineRequest
import com.soloist.proto.gacha.CreateGachaMachineRequest
import com.soloist.proto.gacha.CreateGachaMachineResponse
import com.soloist.proto.gacha.CreateGearItemRequest
import com.soloist.proto.gacha.CreateGearItemResponse
import com.soloist.proto.gacha.GachaServiceGrpc
import com.soloist.proto.gacha.GetGachaMachinesResponse
import com.soloist.proto.gacha.GetInventoryRequest
import com.soloist.proto.gacha.GetInventoryResponse
import com.soloist.proto.gacha.ListGachaMachinesResponse
import com.soloist.proto.gacha.ListGearItemsRequest
import com.soloist.proto.gacha.ListGearItemsResponse
import com.soloist.proto.gacha.OpenGachaRequest
import com.soloist.proto.gacha.OpenGachaResponse
import com.soloist.proto.gacha.RemoveItemFromMachineRequest
import io.grpc.stub.StreamObserver
import org.springframework.grpc.server.service.GrpcService
import java.util.UUID

@GrpcService
class GachaController(
	private val protoMapper: ProtoMapper,
	private val gachaService: GachaService,
	private val playerInventoryService: PlayerInventoryService,
	private val gearItemService: GearItemService,
	private val gachaMachineService: GachaMachineService
) : GachaServiceGrpc.GachaServiceImplBase() {

	override fun getGachaMachines(
		request: Empty,
		responseObserver: StreamObserver<GetGachaMachinesResponse>
	) {
		val machines = gachaService.getActiveMachines()
			.map { protoMapper.map(it) }

		val response = GetGachaMachinesResponse.newBuilder()
			.addAllMachines(machines)
			.build()

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}

	override fun openGacha(
		request: OpenGachaRequest,
		responseObserver: StreamObserver<OpenGachaResponse>
	) {
		val result = gachaService.openGacha(
			playerId = request.playerId,
			machineId = UUID.fromString(request.machineId),
			count = request.count
		)

		val response = OpenGachaResponse.newBuilder()
			.addAllObtainedItems(result.obtainedItems.map { protoMapper.map(it) })
			.setBalanceAfter(result.balanceAfter.toPlainString())
			.build()

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}

	override fun getInventory(
		request: GetInventoryRequest,
		responseObserver: StreamObserver<GetInventoryResponse>
	) {
		val page = playerInventoryService.getInventory(
			playerId = request.playerId,
			pageIndex = request.paging.page,
			pageSize = request.paging.pageSize
		)

		val response = GetInventoryResponse.newBuilder()
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
}
