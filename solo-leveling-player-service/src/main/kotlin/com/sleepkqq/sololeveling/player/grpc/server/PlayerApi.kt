package com.sleepkqq.sololeveling.player.grpc.server

import com.google.protobuf.Empty
import com.sleepkqq.sololeveling.config.interceptor.UserContextHolder
import com.sleepkqq.sololeveling.jimmer.enums.EnumLocalizer
import com.sleepkqq.sololeveling.player.lozalization.LocalizationCodes.TABLES_PLAYER_BALANCE_TRANSACTIONS
import com.sleepkqq.sololeveling.player.lozalization.LocalizationCodes.TABLES_PLAYER_TASKS
import com.sleepkqq.sololeveling.player.mapper.ProtoMapper
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerBalanceTransaction.AMOUNT_FIELD
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerBalanceTransactionView
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerBalanceView
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerTaskView
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic
import com.sleepkqq.sololeveling.player.model.repository.player.PlayerBalanceTransactionRepository
import com.sleepkqq.sololeveling.player.model.repository.player.PlayerTaskRepository
import com.sleepkqq.sololeveling.player.service.player.PlayerBalanceService
import com.sleepkqq.sololeveling.player.service.player.PlayerBalanceTransactionService
import com.sleepkqq.sololeveling.player.service.player.PlayerTaskService
import com.sleepkqq.sololeveling.player.service.player.PlayerTaskTopicService
import com.sleepkqq.sololeveling.proto.player.*
import io.grpc.stub.StreamObserver
import org.springframework.grpc.server.service.GrpcService
import java.util.UUID

@GrpcService
class PlayerApi(
	private val playerTaskService: PlayerTaskService,
	private val playerTaskTopicService: PlayerTaskTopicService,
	private val protoMapper: ProtoMapper,
	private val playerBalanceTransactionService: PlayerBalanceTransactionService,
	private val playerBalanceService: PlayerBalanceService,
	private val enumLocalizer: EnumLocalizer
) : PlayerServiceGrpc.PlayerServiceImplBase() {

	override fun getActiveTasks(
		request: Empty,
		responseObserver: StreamObserver<GetActiveTasksResponse>
	) {
		val activeTasks = playerTaskService.getActiveTasks(UserContextHolder.getUserId()!!)
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
		request: Empty,
		responseObserver: StreamObserver<GetPlayerTopicsResponse>
	) {
		val topics = playerTaskTopicService.getByPlayerId(UserContextHolder.getUserId()!!)
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
		val receivedTopics = request.playerTaskTopicsList
			.map(protoMapper::map)
			.map { it.toEntity() }

		val disabledTopics = TaskTopic.getDisabledTopics()
		if (receivedTopics.any { it.taskTopic() in disabledTopics }) {
			throw IllegalArgumentException("Cannot select disabled topics")
		}

		playerTaskTopicService.updateAll(receivedTopics)

		responseObserver.onNext(Empty.newBuilder().build())
		responseObserver.onCompleted()
	}

	override fun generateTasks(
		request: Empty,
		responseObserver: StreamObserver<Empty>
	) {
		playerTaskService.generateTasks(UserContextHolder.getUserId()!!)

		responseObserver.onNext(Empty.newBuilder().build())
		responseObserver.onCompleted()
	}

	override fun completeTask(
		request: CompleteTaskRequest,
		responseObserver: StreamObserver<CompleteTaskResponse>
	) {
		val playerStates = playerTaskService.completeTask(
			UserContextHolder.getUserId()!!,
			UUID.fromString(request.playerTaskId)
		)

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
		playerTaskService.skipTask(
			UserContextHolder.getUserId()!!,
			UUID.fromString(request.playerTaskId)
		)

		responseObserver.onNext(Empty.newBuilder().build())
		responseObserver.onCompleted()
	}

	override fun getPlayerBalance(
		request: Empty,
		responseObserver: StreamObserver<GetPlayerBalanceResponse>
	) {
		val playerBalance = playerBalanceService.getView(
			UserContextHolder.getUserId()!!,
			PlayerBalanceView::class
		)
		val grpcResponse = GetPlayerBalanceResponse.newBuilder()
			.setBalance(protoMapper.map(playerBalance))
			.build()

		responseObserver.onNext(grpcResponse)
		responseObserver.onCompleted()
	}

	override fun searchPlayerBalanceTransactions(
		request: SearchEntitiesRequest,
		responseObserver: StreamObserver<SearchPlayerBalanceTransactionsResponse>
	) {
		val transactionsPage = playerBalanceTransactionService.searchView(
			UserContextHolder.getUserId()!!,
			request.options,
			request.paging,
			PlayerBalanceTransactionView::class
		)
		val response = protoMapper.mapTransactions(
			transactionsPage,
			request.paging.page,
			enumLocalizer.localize(
				TABLES_PLAYER_BALANCE_TRANSACTIONS,
				PlayerBalanceTransactionRepository.FIELD_ENUM_TYPES
			),
			setOf(AMOUNT_FIELD)
		)

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}

	override fun searchPlayerTasks(
		request: SearchEntitiesRequest,
		responseObserver: StreamObserver<SearchPlayerTasksResponse>
	) {
		val tasksPage = playerTaskService.searchView(
			UserContextHolder.getUserId()!!,
			request.options,
			request.paging,
			PlayerTaskView::class
		)
		val response = protoMapper.mapTasks(
			tasksPage,
			request.paging.page,
			enumLocalizer.localize(
				TABLES_PLAYER_TASKS,
				PlayerTaskRepository.FIELD_ENUM_TYPES,
				PlayerTaskRepository.ENUM_TYPE_PREDICATES
			)
		)

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}
}
