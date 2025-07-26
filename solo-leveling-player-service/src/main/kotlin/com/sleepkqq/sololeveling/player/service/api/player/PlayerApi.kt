package com.sleepkqq.sololeveling.player.service.api.player

import com.google.protobuf.Empty
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerTaskStatus
import com.sleepkqq.sololeveling.player.service.kafka.producer.GenerateTasksProducer
import com.sleepkqq.sololeveling.player.service.mapper.ProtoMapper
import com.sleepkqq.sololeveling.player.service.service.player.PlayerService
import com.sleepkqq.sololeveling.player.service.service.player.PlayerTaskService
import com.sleepkqq.sololeveling.player.service.service.player.PlayerTaskTopicService
import com.sleepkqq.sololeveling.proto.player.CompleteTaskRequest
import com.sleepkqq.sololeveling.proto.player.GenerateTasksRequest
import com.sleepkqq.sololeveling.proto.player.GetCurrentTasksRequest
import com.sleepkqq.sololeveling.proto.player.GetCurrentTasksResponse
import com.sleepkqq.sololeveling.proto.player.GetPlayerInfoRequest
import com.sleepkqq.sololeveling.proto.player.GetPlayerInfoResponse
import com.sleepkqq.sololeveling.proto.player.PlayerServiceGrpc.PlayerServiceImplBase
import com.sleepkqq.sololeveling.proto.player.SavePlayerTopicsRequest
import com.sleepkqq.sololeveling.proto.player.SkipTaskRequest
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import org.springframework.grpc.server.service.GrpcService
import org.springframework.transaction.annotation.Transactional

@GrpcService
class PlayerApi(
	private val playerService: PlayerService,
	private val playerTaskService: PlayerTaskService,
	private val playerTaskTopicService: PlayerTaskTopicService,
	private val generateTasksProducer: GenerateTasksProducer,
	private val protoMapper: ProtoMapper
) : PlayerServiceImplBase() {

	private val log = LoggerFactory.getLogger(javaClass)

	@Transactional
	override fun getPlayerInfo(
		request: GetPlayerInfoRequest,
		responseObserver: StreamObserver<GetPlayerInfoResponse>
	) {
		try {
			val player = playerService.get(request.playerId)
			val response = GetPlayerInfoResponse.newBuilder()
				.setPlayerInfo(protoMapper.map(player))
				.build()

			responseObserver.onNext(response)
			responseObserver.onCompleted()
		} catch (e: Exception) {
			log.error("getPlayerInfo error", e)
			responseObserver.onError(e)
		}
	}

	@Transactional
	override fun getCurrentTasks(
		request: GetCurrentTasksRequest,
		responseObserver: StreamObserver<GetCurrentTasksResponse>
	) {
		try {
			val currentTasks = playerTaskService.getCurrentTasks(request.playerId)
			val response = GetCurrentTasksResponse.newBuilder()
				.addAllCurrentTask(currentTasks.map(protoMapper::map))
				.build()

			responseObserver.onNext(response)
			responseObserver.onCompleted()
		} catch (e: Exception) {
			log.error("getCurrentTasks error", e)
			responseObserver.onError(e)
		}
	}

	@Transactional
	override fun savePlayerTopics(
		request: SavePlayerTopicsRequest,
		responseObserver: StreamObserver<Empty>
	) {
		try {
			request.topicList
				.map(protoMapper::map)
				.map { playerTaskTopicService.initialize(request.playerId, it) }
				.forEach { playerTaskTopicService.insert(it) }
			val response = Empty.newBuilder().build()

			responseObserver.onNext(response)
			responseObserver.onCompleted()
		} catch (e: Exception) {
			log.error("savePlayerTopics error", e)
			responseObserver.onError(e)
		}
	}

	@Transactional
	override fun generateTasks(
		request: GenerateTasksRequest,
		responseObserver: StreamObserver<Empty>
	) {
		try {
			generateTasksProducer.send(request.playerId)
			val response = Empty.newBuilder().build()

			responseObserver.onNext(response)
			responseObserver.onCompleted()
		} catch (e: Exception) {
			log.error("savePlayerTopics error", e)
			responseObserver.onError(e)
		}
	}

	@Transactional
	override fun completeTask(
		request: CompleteTaskRequest,
		responseObserver: StreamObserver<Empty>
	) {
		try {
			val playerTaskId = protoMapper.map(request.playerTaskId)
			playerTaskService.setStatus(setOf(playerTaskId), PlayerTaskStatus.PENDING_COMPLETION)
			val response = Empty.newBuilder().build()

			responseObserver.onNext(response)
			responseObserver.onCompleted()
		} catch (e: Exception) {
			log.error("completeTask error", e)
			responseObserver.onError(e)
		}
	}

	@Transactional
	override fun skipTask(
		request: SkipTaskRequest,
		responseObserver: StreamObserver<Empty>
	) {
		try {
			val playerTaskId = protoMapper.map(request.playerTaskId)
			playerTaskService.setStatus(setOf(playerTaskId), PlayerTaskStatus.SKIPPED)
			val response = Empty.newBuilder().build()

			responseObserver.onNext(response)
			responseObserver.onCompleted()
		} catch (e: Exception) {
			log.error("completeTask error", e)
			responseObserver.onError(e)
		}
	}
}
