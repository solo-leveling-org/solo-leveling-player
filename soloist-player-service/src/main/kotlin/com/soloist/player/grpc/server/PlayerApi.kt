package com.soloist.player.grpc.server

import com.google.protobuf.Empty
import com.soloist.avro.task.SaveTasksOperation
import com.soloist.config.interceptor.UserContextHolder
import com.soloist.jimmer.enums.EnumLocalizer
import com.soloist.player.config.properties.PlayerLimitsProperties
import com.soloist.player.service.i18n.LocalizationCode
import com.soloist.player.mapper.ProtoMapper
import com.soloist.player.model.entity.player.PlayerBalanceTransaction.AMOUNT_FIELD
import com.soloist.player.model.entity.player.dto.PlayerBalanceTransactionView
import com.soloist.player.model.entity.player.dto.PlayerBalanceView
import com.soloist.player.model.entity.player.dto.PlayerDailyTaskView
import com.soloist.player.model.entity.player.dto.PlayerStaminaView
import com.soloist.player.model.entity.player.dto.PlayerTaskView
import com.soloist.player.model.entity.task.enums.TaskTopic
import com.soloist.player.model.repository.player.PlayerBalanceTransactionRepository
import com.soloist.player.model.repository.player.PlayerTaskRepository
import com.soloist.player.service.task.TaskService
import com.soloist.proto.player.*
import com.soloist.player.service.player.PlayerBalanceService
import com.soloist.player.service.player.PlayerBalanceTransactionService
import com.soloist.player.service.player.PlayerDailyTaskService
import com.soloist.player.service.player.PlayerDayActivityService
import com.soloist.player.service.player.PlayerService
import com.soloist.player.service.player.PlayerStaminaService
import com.soloist.player.service.player.PlayerTaskService
import com.soloist.player.service.player.PlayerTaskTopicService
import io.grpc.stub.StreamObserver
import org.springframework.grpc.server.service.GrpcService
import java.util.*

@GrpcService
class PlayerApi(
	private val playerTaskService: PlayerTaskService,
	private val playerTaskTopicService: PlayerTaskTopicService,
	private val protoMapper: ProtoMapper,
	private val playerBalanceTransactionService: PlayerBalanceTransactionService,
	private val playerBalanceService: PlayerBalanceService,
	private val taskService: TaskService,
	private val enumLocalizer: EnumLocalizer,
	private val playerStaminaService: PlayerStaminaService,
	private val playerLimitsProperties: PlayerLimitsProperties,
	private val playerService: PlayerService,
	private val playerDailyTaskService: PlayerDailyTaskService,
	private val playerDayActivityService: PlayerDayActivityService
) : PlayerServiceGrpc.PlayerServiceImplBase() {

	override fun getActiveTasks(
		request: Empty,
		responseObserver: StreamObserver<GetActiveTasksResponse>
	) {
		val playerId = UserContextHolder.getUserId()!!

		val activeTasks = playerTaskService.getActiveTasks(playerId, PlayerTaskView::class)
			.map(protoMapper::map)

		val isFirstTime = activeTasks.isEmpty()

		val stamina = playerStaminaService.getView(playerId, PlayerStaminaView::class)
			.let { playerStaminaService.calculateCurrent(it.toEntity()) }
			.let { PlayerStaminaView(it) }
		val staminaConfig = playerLimitsProperties.limits.free.stamina

		val response = GetActiveTasksResponse.newBuilder()
			.addAllTasks(activeTasks)
			.setIsFirstTime(isFirstTime)
			.setStamina(protoMapper.map(stamina, staminaConfig))
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
		playerTaskService.generateTasks(
			playerId = UserContextHolder.getUserId()!!,
			operation = SaveTasksOperation.INITIALIZE
		)

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
		val response = GetPlayerBalanceResponse.newBuilder()
			.setBalance(protoMapper.map(playerBalance))
			.build()

		responseObserver.onNext(response)
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
				LocalizationCode.TABLES_PLAYER_BALANCE_TRANSACTIONS,
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
				LocalizationCode.TABLES_PLAYER_TASKS,
				PlayerTaskRepository.FIELD_ENUM_TYPES,
				PlayerTaskRepository.ENUM_TYPE_PREDICATES
			)
		)

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

	override fun resetPlayer(
		request: ResetPlayerRequest,
		responseObserver: StreamObserver<Empty>
	) {
		playerService.reset(request.playerId)

		responseObserver.onNext(Empty.newBuilder().build())
		responseObserver.onCompleted()
	}

	override fun getDailyTasks(
		request: Empty,
		responseObserver: StreamObserver<GetDailyTasksResponse>
	) {
		val dailyTasks = playerDailyTaskService.findView(
			UserContextHolder.getUserId()!!,
			PlayerDailyTaskView::class
		)

		val sortedMappedTasks = dailyTasks.sortedBy { it.type.ordinal }
			.map { protoMapper.map(it) }

		val response = GetDailyTasksResponse.newBuilder()
			.addAllTasks(sortedMappedTasks)
			.build()

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}

	override fun getMonthlyActivity(
		request: GetMonthlyActivityRequest,
		responseObserver: StreamObserver<GetMonthlyActivityResponse>
	) {
		val monthlyActivity = playerDayActivityService.getMonthlyActivity(
			UserContextHolder.getUserId()!!,
			year = request.year,
			month = request.month
		)

		val response = GetMonthlyActivityResponse.newBuilder()
			.addAllActiveDays(monthlyActivity)
			.build()

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}
}
