package com.sleepkqq.sololeveling.player.service.api.player

import com.google.protobuf.Empty
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTaskTopic
import com.sleepkqq.sololeveling.player.service.mapper.ProtoMapper
import com.sleepkqq.sololeveling.player.service.service.player.PlayerService
import com.sleepkqq.sololeveling.player.service.service.player.PlayerTaskService
import com.sleepkqq.sololeveling.player.service.service.player.PlayerTaskStatusService
import com.sleepkqq.sololeveling.player.service.service.player.PlayerTaskTopicService
import com.sleepkqq.sololeveling.proto.player.*
import com.sleepkqq.sololeveling.proto.player.PlayerServiceGrpc.PlayerServiceImplBase
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import org.springframework.grpc.server.service.GrpcService

@Suppress("unused")
@GrpcService
class PlayerApi(
	private val playerService: PlayerService,
	private val playerTaskService: PlayerTaskService,
	private val playerTaskStatusService: PlayerTaskStatusService,
	private val playerTaskTopicService: PlayerTaskTopicService,
	private val protoMapper: ProtoMapper
) : PlayerServiceImplBase() {

	private val log = LoggerFactory.getLogger(javaClass)

	override fun getActiveTasks(
		request: GetActiveTasksRequest,
		responseObserver: StreamObserver<GetActiveTasksResponse>
	) {
		log.info(">> getActiveTasks called by user={}", request.playerId)

		try {
			val activeTasks = playerTaskService.getActiveTasks(request.playerId)
				.map { protoMapper.map(it) }

			val tasksCount = playerTaskService.getTasksCount(request.playerId)
			val isFirstTime = tasksCount == 0L

			val response = GetActiveTasksResponse.newBuilder()
				.addAllTasks(activeTasks)
				.setFirstTime(isFirstTime)
				.build()

			responseObserver.onNext(response)
			responseObserver.onCompleted()

		} catch (e: Exception) {
			log.error("getCurrentTasks error", e)
			responseObserver.onError(e)
		}
	}

	override fun getPlayerTopics(
		request: GetPlayerTopicsRequest,
		responseObserver: StreamObserver<GetPlayerTopicsResponse>
	) {
		log.info(">> getPlayerTopics called by user={}", request.playerId)

		try {
			val topics = playerTaskTopicService.getActiveTopics(request.playerId)
				.map { protoMapper.map(it) }

			val response = GetPlayerTopicsResponse.newBuilder()
				.addAllPlayerTaskTopics(topics)
				.build()

			responseObserver.onNext(response)
			responseObserver.onCompleted()

		} catch (e: Exception) {
			log.error("getPlayerTopics error", e)
			responseObserver.onError(e)
		}
	}

	override fun savePlayerTopics(
		request: SavePlayerTopicsRequest,
		responseObserver: StreamObserver<Empty>
	) {
		log.info(">> savePlayerTopics called by user={}", request.playerId)

		try {
			val receivedEnumTopics = request.topicsList.map(protoMapper::map)

			val initialTopics = playerTaskTopicService.getTopics(request.playerId)

			val restoredTopics = initialTopics.map {
				PlayerTaskTopic(it)
				{ isActive = it.taskTopic in receivedEnumTopics }
			}

			val initialEnumTopics = initialTopics.map { it.taskTopic }
			val newTopics = receivedEnumTopics
				.filter { it !in initialEnumTopics }
				.map { playerTaskTopicService.initialize(request.playerId, it) }

			playerTaskTopicService.saveAll(restoredTopics + newTopics)

			responseObserver.onNext(Empty.newBuilder().build())
			responseObserver.onCompleted()

		} catch (e: Exception) {
			log.error("savePlayerTopics error", e)
			responseObserver.onError(e)
		}
	}

	override fun generateTasks(
		request: GenerateTasksRequest,
		responseObserver: StreamObserver<Empty>
	) {
		log.info(">> generateTasks called by user={}", request.playerId)

		try {
			playerTaskStatusService.generateTasks(request.playerId)

			responseObserver.onNext(Empty.newBuilder().build())
			responseObserver.onCompleted()

		} catch (e: Exception) {
			log.error("savePlayerTopics error", e)
			responseObserver.onError(e)
		}
	}

	override fun completeTask(
		request: CompleteTaskRequest,
		responseObserver: StreamObserver<Empty>
	) {
		log.info(">> completeTask task={} called by user={}", request.playerTask.id, request.playerId)

		try {
			val playerTask = protoMapper.map(request.playerTask)
				.toEntity()
			playerTaskStatusService.pendingCompleteTask(playerTask, request.playerId)

			responseObserver.onNext(Empty.newBuilder().build())
			responseObserver.onCompleted()

		} catch (e: Exception) {
			log.error("completeTask error", e)
			responseObserver.onError(e)
		}
	}

	override fun skipTask(
		request: SkipTaskRequest,
		responseObserver: StreamObserver<Empty>
	) {
		log.info(">> skipTask task={} called by user={}", request.playerTask.id, request.playerId)

		try {
			val playerTask = protoMapper.map(request.playerTask)
				.toEntity()
			playerTaskStatusService.skipTask(playerTask, request.playerId)

			responseObserver.onNext(Empty.newBuilder().build())
			responseObserver.onCompleted()

		} catch (e: Exception) {
			log.error("completeTask error", e)
			responseObserver.onError(e)
		}
	}
}
