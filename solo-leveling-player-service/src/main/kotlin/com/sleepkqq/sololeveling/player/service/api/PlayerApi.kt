package com.sleepkqq.sololeveling.player.service.api

import com.google.protobuf.Empty
import com.sleepkqq.sololeveling.jimmer.enums.EnumLocalizer
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerBalanceTransactionView
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerBalanceView
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerBalanceTransactionCause
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerBalanceTransactionType
import com.sleepkqq.sololeveling.player.service.mapper.ProtoMapper
import com.sleepkqq.sololeveling.player.service.service.player.PlayerBalanceService
import com.sleepkqq.sololeveling.player.service.service.player.PlayerBalanceTransactionService
import com.sleepkqq.sololeveling.player.service.service.player.PlayerService
import com.sleepkqq.sololeveling.player.service.service.player.PlayerTaskService
import com.sleepkqq.sololeveling.player.service.service.player.PlayerTaskStatusService
import com.sleepkqq.sololeveling.player.service.service.player.PlayerTaskTopicService
import com.sleepkqq.sololeveling.proto.player.CompleteTaskRequest
import com.sleepkqq.sololeveling.proto.player.CompleteTaskResponse
import com.sleepkqq.sololeveling.proto.player.GenerateTasksRequest
import com.sleepkqq.sololeveling.proto.player.GetActiveTasksRequest
import com.sleepkqq.sololeveling.proto.player.GetActiveTasksResponse
import com.sleepkqq.sololeveling.proto.player.GetPlayerBalanceRequest
import com.sleepkqq.sololeveling.proto.player.GetPlayerBalanceResponse
import com.sleepkqq.sololeveling.proto.player.GetPlayerTopicsRequest
import com.sleepkqq.sololeveling.proto.player.GetPlayerTopicsResponse
import com.sleepkqq.sololeveling.proto.player.PlayerServiceGrpc
import com.sleepkqq.sololeveling.proto.player.SavePlayerTopicsRequest
import com.sleepkqq.sololeveling.proto.player.SearchPlayerBalanceTransactionsRequest
import com.sleepkqq.sololeveling.proto.player.SearchPlayerBalanceTransactionsResponse
import com.sleepkqq.sololeveling.proto.player.SkipTaskRequest
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
	private val protoMapper: ProtoMapper,
	private val playerBalanceTransactionService: PlayerBalanceTransactionService,
	private val playerBalanceService: PlayerBalanceService,
	private val enumLocalizer: EnumLocalizer
) : PlayerServiceGrpc.PlayerServiceImplBase() {

	private val log = LoggerFactory.getLogger(javaClass)

	override fun getActiveTasks(
		request: GetActiveTasksRequest,
		responseObserver: StreamObserver<GetActiveTasksResponse>
	) {
		log.info(">> getActiveTasks called by user={}", request.playerId)

		val activeTasks = playerTaskService.getActiveTasks(request.playerId)
			.map { protoMapper.map(it) }

		val isFirstTime = activeTasks.isEmpty()

		val response = GetActiveTasksResponse.newBuilder()
			.addAllTasks(activeTasks)
			.setFirstTime(isFirstTime)
			.build()

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}

	override fun getPlayerTopics(
		request: GetPlayerTopicsRequest,
		responseObserver: StreamObserver<GetPlayerTopicsResponse>
	) {
		log.info(">> getPlayerTopics called by user={}", request.playerId)

		val topics = playerTaskTopicService.getByPlayerId(request.playerId)
			.map { protoMapper.map(it) }

		val response = GetPlayerTopicsResponse.newBuilder()
			.addAllPlayerTaskTopics(topics)
			.build()

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}

	override fun savePlayerTopics(
		request: SavePlayerTopicsRequest,
		responseObserver: StreamObserver<Empty>
	) {
		log.info(">> savePlayerTopics called by user={}", request.playerId)

		val receivedTopics = request.playerTaskTopicsList
			.map(protoMapper::map)
			.map { it.toEntity() }

		playerTaskTopicService.updateAll(receivedTopics)

		responseObserver.onNext(Empty.newBuilder().build())
		responseObserver.onCompleted()
	}

	override fun generateTasks(
		request: GenerateTasksRequest,
		responseObserver: StreamObserver<Empty>
	) {
		log.info(">> generateTasks called by user={}", request.playerId)

		playerTaskStatusService.generateTasks(request.playerId)

		responseObserver.onNext(Empty.newBuilder().build())
		responseObserver.onCompleted()
	}

	override fun completeTask(
		request: CompleteTaskRequest,
		responseObserver: StreamObserver<CompleteTaskResponse>
	) {
		log.info(">> completeTask task={} called by user={}", request.playerTask.id, request.playerId)

		val playerTask = protoMapper.map(request.playerTask)
			.toEntity()

		val playerStates = playerTaskStatusService.pendingCompleteTask(playerTask, request.playerId)

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
		log.info(">> skipTask task={} called by user={}", request.playerTask.id, request.playerId)

		val playerTask = protoMapper.map(request.playerTask)
			.toEntity()
		playerTaskStatusService.skipTask(playerTask, request.playerId)

		responseObserver.onNext(Empty.newBuilder().build())
		responseObserver.onCompleted()
	}

	override fun getPlayerBalance(
		request: GetPlayerBalanceRequest,
		responseObserver: StreamObserver<GetPlayerBalanceResponse>
	) {
		log.info(">> getPlayerBalance called by user={}", request.playerId)

		val playerBalance = playerBalanceService.getView(request.playerId, PlayerBalanceView::class)
		val grpcResponse = GetPlayerBalanceResponse.newBuilder()
			.setBalance(protoMapper.map(playerBalance))
			.build()

		responseObserver.onNext(grpcResponse)
		responseObserver.onCompleted()
	}

	override fun searchPlayerBalanceTransactions(
		request: SearchPlayerBalanceTransactionsRequest,
		responseObserver: StreamObserver<SearchPlayerBalanceTransactionsResponse>
	) {
		log.info(">> searchPlayerBalanceTransactions called by user={}", request.playerId)

		val transactionsPage = playerBalanceTransactionService.searchView(
			request.playerId,
			request.options,
			PlayerBalanceTransactionView::class
		)
		val response = protoMapper.map(transactionsPage)
			.addAllFilters(
				enumLocalizer.localize(
					mapOf(
						"type" to PlayerBalanceTransactionType::class.java,
						"cause" to PlayerBalanceTransactionCause::class.java
					)
				)
			)
			.build()

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}
}
