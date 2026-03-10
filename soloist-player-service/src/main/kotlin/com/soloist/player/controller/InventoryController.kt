package com.soloist.player.controller

import com.google.protobuf.Empty
import com.soloist.jimmer.enums.EnumLocalizer
import com.soloist.player.mapper.ProtoMapper
import com.soloist.player.model.entity.player.dto.PlayerGearItemView
import com.soloist.player.model.repository.player.PlayerGearItemRepository
import com.soloist.player.service.gear.ConsumableService
import com.soloist.player.service.gear.PlayerGearItemService
import com.soloist.player.service.i18n.LocalizationCode
import com.soloist.proto.inventory.AddConsumableRequest
import com.soloist.proto.inventory.GetConsumablesRequest
import com.soloist.proto.inventory.GetConsumablesResponse
import com.soloist.proto.inventory.InventoryServiceGrpc
import com.soloist.proto.inventory.SearchGearItemsRequest
import com.soloist.proto.inventory.SearchGearItemsResponse
import com.soloist.proto.inventory.UseConsumableRequest
import io.grpc.stub.StreamObserver
import org.springframework.grpc.server.service.GrpcService

@GrpcService
class InventoryController(
	private val protoMapper: ProtoMapper,
	private val playerGearItemService: PlayerGearItemService,
	private val consumableService: ConsumableService,
	private val enumLocalizer: EnumLocalizer
) : InventoryServiceGrpc.InventoryServiceImplBase() {

	override fun searchGearItems(
		request: SearchGearItemsRequest,
		responseObserver: StreamObserver<SearchGearItemsResponse>
	) {
		val page = playerGearItemService.searchView(
			playerId = request.playerId,
			options = request.options,
			paging = request.paging,
			viewType = PlayerGearItemView::class
		)

		val response = protoMapper.mapGearItems(
			page,
			request.paging.page,
			request.paging.pageSize,
			enumLocalizer.localize(
				LocalizationCode.TABLES_GEAR_ITEMS,
				PlayerGearItemRepository.FIELD_ENUM_TYPES
			)
		)

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}

	override fun addConsumable(
		request: AddConsumableRequest,
		responseObserver: StreamObserver<Empty>
	) {
		consumableService.add(
			playerId = request.playerId,
			type = protoMapper.map(request.consumableType),
			quantity = request.quantity
		)

		responseObserver.onNext(Empty.getDefaultInstance())
		responseObserver.onCompleted()
	}

	override fun useConsumable(
		request: UseConsumableRequest,
		responseObserver: StreamObserver<Empty>
	) {
		consumableService.use(
			playerId = request.playerId,
			type = protoMapper.map(request.consumableType)
		)

		responseObserver.onNext(Empty.getDefaultInstance())
		responseObserver.onCompleted()
	}

	override fun getConsumables(
		request: GetConsumablesRequest,
		responseObserver: StreamObserver<GetConsumablesResponse>
	) {
		val consumables = consumableService.find(request.playerId)
			.map { protoMapper.map(it) }

		val response = GetConsumablesResponse.newBuilder()
			.addAllConsumables(consumables)
			.build()

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}
}
