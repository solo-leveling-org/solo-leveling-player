package com.sleepkqq.sololeveling.player

import com.sleepkqq.sololeveling.player.model.entity.Fetchers
import com.sleepkqq.sololeveling.player.model.entity.Immutables
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerTaskView
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerBalanceTransactionCause
import com.sleepkqq.sololeveling.player.model.entity.player.enums.Rarity
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic
import com.sleepkqq.sololeveling.player.model.repository.task.TaskRepository
import com.sleepkqq.sololeveling.player.service.player.PlayerBalanceService
import com.sleepkqq.sololeveling.player.service.player.PlayerService
import com.sleepkqq.sololeveling.player.service.player.PlayerTaskService
import com.sleepkqq.sololeveling.proto.player.EnumFilter
import com.sleepkqq.sololeveling.proto.player.Filter
import com.sleepkqq.sololeveling.proto.player.RequestQueryOptions
import org.assertj.core.api.Assertions.assertThat
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal
import kotlin.time.measureTimedValue

class ApplicationTest : BaseTestClass() {

	@Autowired
	private lateinit var playerService: PlayerService

	@Autowired
	private lateinit var playerBalanceService: PlayerBalanceService

	@Autowired
	private lateinit var playerTaskService: PlayerTaskService

	@Autowired
	private lateinit var taskRepository: TaskRepository

	@Test
	fun `success deposit test`() {
		// Arrange
		val user = createUser(1, "deposit-test")
		val player = user.player()!!
		val playerBalance = player.balance()!!

		createPlayerBalanceTransaction(playerBalance.id())

		val dbPlayer = playerService.get(
			player.id(),
			Fetchers.PLAYER_FETCHER
				.allScalarFields()
				.balance(
					Fetchers.PLAYER_BALANCE_FETCHER
						.allScalarFields()
				)
		)

		val updatedBalance = playerBalanceService.deposit(
			dbPlayer.balance()!!,
			BigDecimal.TWO,
			PlayerBalanceTransactionCause.TASK_COMPLETION
		)

		playerService.update(
			Immutables.createPlayer(dbPlayer) {
				it.setBalance(updatedBalance)
			}
		)

		val playerWithTransactions = playerService.get(
			player.id(),
			Fetchers.PLAYER_FETCHER
				.version()
				.balance(
					Fetchers.PLAYER_BALANCE_FETCHER
						.allScalarFields()
						.transactions(
							Fetchers.PLAYER_BALANCE_TRANSACTION_FETCHER
								.allScalarFields()
						)
				)
		)

		assertThat(playerWithTransactions.version()).isEqualTo(1)
		assertThat(playerWithTransactions.balance()!!.version()).isEqualTo(1)
		assertThat(playerWithTransactions.balance()!!.balance().compareTo(BigDecimal.TWO)).isEqualTo(0)
		assertThat(playerWithTransactions.balance()!!.transactions().size).isEqualTo(2)
	}

	@Test
	fun `success player task search test`() {
		// Given
		val user = createUser(2, "search-test")
		val player = user.player()!!

		val task = createTask(
			experience = 100,
			currencyReward = 50,
			rarity = Rarity.EPIC,
			agility = 5,
			strength = 10,
			intelligence = 3,
			topics = listOf(TaskTopic.PRODUCTIVITY, TaskTopic.MENTAL_HEALTH)
		)

		val playerTask = createPlayerTask(task, player.id())

		playerTaskService.insertAll(listOf(playerTask))

		// When
		val options = RequestQueryOptions.newBuilder()
			.setPage(0)
			.setPageSize(1)
			.setFilter(
				Filter.newBuilder()
					.addEnumFilters(
						EnumFilter.newBuilder()
							.setField("rarity")
							.addValues(task.rarity()!!.name)
					)
					.addEnumFilters(
						EnumFilter.newBuilder()
							.setField("status")
							.addValues(playerTask.status()!!.name)
					)
			)
			.build()

		val searchedTasks = playerTaskService.searchView(player.id(), options, PlayerTaskView::class)

		assertThat(searchedTasks.totalRowCount.toInt()).isEqualTo(1)
	}

	@Test
	fun `find returns task with exact matching rarity and topics`() {
		// Given: Тестовый игрок
		val testUser = createUser(7, "test-player")
		val testPlayer = testUser.player()!!

		// Given: Другой игрок (для назначения задач)
		val otherUser = createUser(8, "other-player")
		val otherPlayer = otherUser.player()!!

		// Given: Неподходящая задача (назначена otherPlayer, rarity=EPIC, но лишний топик)
		val nonMatchingTask = createTask(
			title = "Non-matching",
			description = "With extra topic",
			rarity = Rarity.EPIC,
			topics = listOf(TaskTopic.MENTAL_HEALTH, TaskTopic.PRODUCTIVITY)
		)
		taskRepository.saveEntities(listOf(nonMatchingTask), SaveMode.INSERT_ONLY)

		val nonMatchingPlayerTask = createPlayerTask(nonMatchingTask, otherPlayer.id())
		playerTaskService.insertAll(listOf(nonMatchingPlayerTask))

		// Given: Подходящая задача (назначена otherPlayer, rarity=EPIC, точные топики)
		val matchingTask = createTask(
			title = "Matching",
			description = "Exact topics",
			experience = 100,
			currencyReward = 50,
			rarity = Rarity.EPIC,
			agility = 5,
			strength = 10,
			intelligence = 3,
			topics = listOf(TaskTopic.MENTAL_HEALTH, TaskTopic.EDUCATION)
		)
		taskRepository.saveEntities(listOf(matchingTask), SaveMode.INSERT_ONLY)

		val matchingPlayerTask = createPlayerTask(matchingTask, otherPlayer.id())
		playerTaskService.insertAll(listOf(matchingPlayerTask))

		// Given: Исключаемая задача (назначена testPlayer)
		val excludedTask = createTask(rarity = Rarity.COMMON, topics = emptyList())
		taskRepository.saveEntities(listOf(excludedTask), SaveMode.INSERT_ONLY)

		val excludedPlayerTask = createPlayerTask(excludedTask, testPlayer.id())
		playerTaskService.insertAll(listOf(excludedPlayerTask))

		// When: find для testPlayer (должен найти matchingTask от otherPlayer)
		val testTopics = listOf(TaskTopic.MENTAL_HEALTH, TaskTopic.EDUCATION)

		val (foundTaskId, duration) = measureTimedValue {
			taskRepository.findMatchingTasks(testPlayer.id(), Rarity.EPIC, testTopics)
		}

		println("findMatchingTasks took ${duration.inWholeMilliseconds} ms")

		// Then
		assertThat(foundTaskId).isNotNull
		assertThat(foundTaskId).isEqualTo(matchingTask.id())
		val foundTask = taskRepository.findNullable(
			foundTaskId,
			Fetchers.TASK_FETCHER.allScalarFields()
				.topics(Fetchers.TASK_TOPIC_ITEM_FETCHER.allScalarFields())
		)
		assertThat(foundTask!!.rarity()).isEqualTo(Rarity.EPIC)
		val taskTopics = foundTask.topics()!!.map { it.topic() }
		assertThat(taskTopics).containsExactlyInAnyOrder(TaskTopic.MENTAL_HEALTH, TaskTopic.EDUCATION)
		assertThat(taskTopics.size).isEqualTo(2)
	}

	@Test
	fun `findMatchingTasks returns correct task mappings for multiple player tasks`() {
		// Given: Тестовый игрок с несколькими PlayerTask
		val testUser = createUser(9, "test-player-batch")
		val testPlayer = testUser.player()!!

		// Given: Другой игрок (для назначения уже существующих задач)
		val otherUser = createUser(10, "other-player-batch")
		val otherPlayer = otherUser.player()!!

		// Given: Создаем задачи с разными rarity и topics для testPlayer
		val testTask1 = createTask(
			title = "Test Task 1",
			description = "Player task 1",
			rarity = Rarity.EPIC,
			topics = listOf(TaskTopic.MENTAL_HEALTH, TaskTopic.EDUCATION)
		)
		val testTask2 = createTask(
			title = "Test Task 2",
			description = "Player task 2",
			rarity = Rarity.RARE,
			topics = listOf(TaskTopic.PRODUCTIVITY)
		)
		val testTask3 = createTask(
			title = "Test Task 3",
			description = "Player task 3",
			rarity = Rarity.LEGENDARY,
			topics = listOf(TaskTopic.EXPERIMENTS, TaskTopic.TEAMWORK)
		)

		taskRepository.saveEntities(listOf(testTask1, testTask2, testTask3), SaveMode.INSERT_ONLY)

		val playerTask1 = createPlayerTask(testTask1, testPlayer.id(), order = 1)
		val playerTask2 = createPlayerTask(testTask2, testPlayer.id(), order = 2)
		val playerTask3 = createPlayerTask(testTask3, testPlayer.id(), order = 3)
		playerTaskService.insertAll(listOf(playerTask1, playerTask2, playerTask3))

		// Given: Создаем matching задачи для otherPlayer с точными параметрами
		val matchingTask1 = createTask(
			title = "Matching Task 1",
			description = "Matches task 1",
			rarity = Rarity.EPIC,
			topics = listOf(TaskTopic.MENTAL_HEALTH, TaskTopic.EDUCATION)
		)
		val matchingTask2 = createTask(
			title = "Matching Task 2",
			description = "Matches task 2",
			rarity = Rarity.RARE,
			topics = listOf(TaskTopic.PRODUCTIVITY)
		)
		val matchingTask3 = createTask(
			title = "Matching Task 3",
			description = "Matches task 3",
			rarity = Rarity.LEGENDARY,
			topics = listOf(TaskTopic.EXPERIMENTS, TaskTopic.TEAMWORK)
		)

		taskRepository.saveEntities(
			listOf(matchingTask1, matchingTask2, matchingTask3),
			SaveMode.INSERT_ONLY
		)

		val otherPlayerTask1 = createPlayerTask(matchingTask1, otherPlayer.id(), order = 1)
		val otherPlayerTask2 = createPlayerTask(matchingTask2, otherPlayer.id(), order = 2)
		val otherPlayerTask3 = createPlayerTask(matchingTask3, otherPlayer.id(), order = 3)
		playerTaskService.insertAll(listOf(otherPlayerTask1, otherPlayerTask2, otherPlayerTask3))

		// Given: Создаем non-matching задачу с другими параметрами
		val nonMatchingTask = createTask(
			title = "Non-matching",
			description = "Wrong topics",
			rarity = Rarity.EPIC,
			topics = listOf(TaskTopic.ECOLOGY, TaskTopic.CREATIVITY)
		)
		taskRepository.saveEntities(listOf(nonMatchingTask), SaveMode.INSERT_ONLY)
		val nonMatchingPlayerTask = createPlayerTask(nonMatchingTask, otherPlayer.id(), order = 4)
		playerTaskService.insertAll(listOf(nonMatchingPlayerTask))

		// When: Вызываем findMatchingTasks и замеряем время
		val (result, duration) = measureTimedValue {
			taskRepository.findMatchingTasks(
				testPlayer.id(),
				listOf(playerTask1, playerTask2, playerTask3)
			)
		}

		println("findMatchingTasks took ${duration.inWholeMilliseconds} ms")

		// Then: Проверяем корректность маппинга
		assertThat(result).hasSize(3)
		assertThat(result[playerTask1.id()]).isEqualTo(matchingTask1.id())
		assertThat(result[playerTask2.id()]).isEqualTo(matchingTask2.id())
		assertThat(result[playerTask3.id()]).isEqualTo(matchingTask3.id())

		// Then: Проверяем, что найденные задачи имеют правильные параметры
		val foundTask1 = taskRepository.findNullable(
			result[playerTask1.id()],
			Fetchers.TASK_FETCHER.allScalarFields()
				.topics(Fetchers.TASK_TOPIC_ITEM_FETCHER.allScalarFields())
		)
		assertThat(foundTask1!!.rarity()).isEqualTo(Rarity.EPIC)
		assertThat(foundTask1.topics()!!.map { it.topic() })
			.containsExactlyInAnyOrder(TaskTopic.MENTAL_HEALTH, TaskTopic.EDUCATION)

		val foundTask2 = taskRepository.findNullable(
			result[playerTask2.id()],
			Fetchers.TASK_FETCHER.allScalarFields()
				.topics(Fetchers.TASK_TOPIC_ITEM_FETCHER.allScalarFields())
		)
		assertThat(foundTask2!!.rarity()).isEqualTo(Rarity.RARE)
		assertThat(foundTask2.topics()!!.map { it.topic() })
			.containsExactly(TaskTopic.PRODUCTIVITY)
	}
}
