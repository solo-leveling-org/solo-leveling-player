package com.soloist.player.controller

import com.soloist.player.config.properties.PlayerLimitsProperties
import com.soloist.player.mapper.ProtoMapper
import com.soloist.player.model.entity.player.dto.DayStreakView
import com.soloist.player.model.entity.player.dto.StaminaView
import com.soloist.player.model.entity.player.dto.PlayerView
import com.soloist.player.service.player.DayActivityService
import com.soloist.player.service.player.DayStreakService
import com.soloist.player.service.player.PlayerService
import com.soloist.player.service.player.StaminaService
import com.soloist.proto.player.GetDayStreakRequest
import com.soloist.proto.player.GetDayStreakResponse
import com.soloist.proto.player.GetMonthlyActivityRequest
import com.soloist.proto.player.GetMonthlyActivityResponse
import com.soloist.proto.player.GetPlayerRequest
import com.soloist.proto.player.GetPlayerResponse
import com.soloist.proto.player.GetStaminaRequest
import com.soloist.proto.player.GetStaminaResponse
import com.soloist.proto.player.PlayerServiceGrpc
import io.grpc.stub.StreamObserver
import org.springframework.grpc.server.service.GrpcService

@GrpcService
class PlayerController(
	private val protoMapper: ProtoMapper,
	private val playerService: PlayerService,
	private val dayActivityService: DayActivityService,
	private val staminaService: StaminaService,
	private val playerLimitsProperties: PlayerLimitsProperties,
	private val dayStreakService: DayStreakService
) : PlayerServiceGrpc.PlayerServiceImplBase() {

	override fun getPlayer(
		request: GetPlayerRequest,
		responseObserver: StreamObserver<GetPlayerResponse>
	) {
		val player = playerService.getView(request.playerId, PlayerView::class)

		val response = GetPlayerResponse.newBuilder()
			.setPlayer(protoMapper.map(player))
			.build()

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}

	override fun getMonthlyActivity(
		request: GetMonthlyActivityRequest,
		responseObserver: StreamObserver<GetMonthlyActivityResponse>
	) {
		val monthlyActivity = dayActivityService.getMonthlyActivity(
			playerId = request.playerId,
			year = request.year,
			month = request.month
		)

		val response = GetMonthlyActivityResponse.newBuilder()
			.addAllActiveDays(monthlyActivity)
			.build()

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}

	override fun getStamina(
		request: GetStaminaRequest,
		responseObserver: StreamObserver<GetStaminaResponse>
	) {
		val stamina = staminaService.getView(request.playerId, StaminaView::class)
			.let { staminaService.calculateCurrent(it.toEntity()) }
			.let { StaminaView(it) }
		val staminaConfig = playerLimitsProperties.limits.free.stamina

		val response = GetStaminaResponse.newBuilder()
			.setStamina(protoMapper.map(stamina, staminaConfig))
			.build()

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}

	override fun getDayStreak(
		request: GetDayStreakRequest,
		responseObserver: StreamObserver<GetDayStreakResponse>
	) {
		val dayStreak = dayStreakService.getView(request.playerId, DayStreakView::class)

		val response = GetDayStreakResponse.newBuilder()
			.setDayStreak(protoMapper.map(dayStreak))
			.build()

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}
}
