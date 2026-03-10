package com.soloist.player.controller

import com.google.protobuf.Empty
import com.soloist.player.config.properties.PlayerLimitsProperties
import com.soloist.player.mapper.ProtoMapper
import com.soloist.player.model.entity.player.dto.DayStreakView
import com.soloist.player.model.entity.player.dto.LevelView
import com.soloist.player.model.entity.player.dto.StaminaView
import com.soloist.player.model.entity.player.dto.PlayerView
import com.soloist.player.model.entity.task.dto.PlayerTaskTopicView
import com.soloist.player.model.entity.task.enums.TaskTopic.DISABLED_TOPICS
import com.soloist.player.service.player.LevelService
import com.soloist.player.service.player.DayActivityService
import com.soloist.player.service.player.DayStreakService
import com.soloist.player.service.player.PlayerService
import com.soloist.player.service.player.StaminaService
import com.soloist.player.service.task.PlayerTaskTopicService
import com.soloist.proto.player.*
import io.grpc.stub.StreamObserver
import org.springframework.grpc.server.service.GrpcService

@GrpcService
class PlayerController(
	private val protoMapper: ProtoMapper,
	private val playerService: PlayerService,
	private val playerTaskTopicService: PlayerTaskTopicService,
	private val dayActivityService: DayActivityService,
	private val staminaService: StaminaService,
	private val playerLimitsProperties: PlayerLimitsProperties,
	private val levelService: LevelService,
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

	override fun getLevel(
		request: GetLevelRequest,
		responseObserver: StreamObserver<GetLevelResponse>
	) {
		val level = levelService.getView(request.playerId, LevelView::class)

		val response = GetLevelResponse.newBuilder()
			.setLevel(protoMapper.map(level))
			.build()

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}

	override fun getPlayerTopics(
		request: GetPlayerTopicsRequest,
		responseObserver: StreamObserver<GetPlayerTopicsResponse>
	) {
		val topics = playerTaskTopicService.findView(request.playerId, PlayerTaskTopicView::class)
			.map(protoMapper::map)

		val response = GetPlayerTopicsResponse.newBuilder()
			.addAllTaskTopics(topics)
			.build()

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}

	override fun savePlayerTopics(
		request: SavePlayerTopicsRequest,
		responseObserver: StreamObserver<Empty>
	) {
		val receivedTopics = request.taskTopicsList
			.map(protoMapper::map)
			.map { it.toEntity() }

		if (receivedTopics.any { it.taskTopic() in DISABLED_TOPICS }) {
			throw IllegalArgumentException("Cannot select disabled topics")
		}

		playerTaskTopicService.updateAll(receivedTopics)

		responseObserver.onNext(Empty.newBuilder().build())
		responseObserver.onCompleted()
	}
}
