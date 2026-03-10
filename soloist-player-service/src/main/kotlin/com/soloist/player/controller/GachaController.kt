package com.soloist.player.controller

import com.google.protobuf.Empty
import com.soloist.player.mapper.ProtoMapper
import com.soloist.player.service.gacha.GachaService
import com.soloist.proto.gacha.GachaServiceGrpc
import com.soloist.proto.gacha.GetGachaMachinesResponse
import com.soloist.proto.gacha.OpenGachaRequest
import com.soloist.proto.gacha.OpenGachaResponse
import io.grpc.stub.StreamObserver
import org.springframework.grpc.server.service.GrpcService
import java.util.UUID

@GrpcService
class GachaController(
	private val protoMapper: ProtoMapper,
	private val gachaService: GachaService
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
}
