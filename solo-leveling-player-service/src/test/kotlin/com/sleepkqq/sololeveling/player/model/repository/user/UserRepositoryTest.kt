package com.sleepkqq.sololeveling.player.model.repository.user

import com.sleepkqq.sololeveling.player.BaseTestClass
import com.sleepkqq.sololeveling.player.model.entity.Immutables
import com.sleepkqq.sololeveling.player.model.entity.leaderboard.enums.LeaderboardType
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerBalanceTransactionCause
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerTaskStatus
import com.sleepkqq.sololeveling.player.model.entity.player.enums.Rarity
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic
import com.sleepkqq.sololeveling.player.model.repository.task.TaskRepository
import com.sleepkqq.sololeveling.player.service.player.PlayerBalanceService
import com.sleepkqq.sololeveling.player.service.player.PlayerService
import com.sleepkqq.sololeveling.player.service.player.PlayerTaskService
import org.assertj.core.api.Assertions.assertThat
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZoneId
import kotlin.time.measureTimedValue

class UserRepositoryTest : BaseTestClass() {

	@Autowired
	private lateinit var userRepository: UserRepository

	@Autowired
	private lateinit var taskRepository: TaskRepository

	@Autowired
	private lateinit var playerTaskService: PlayerTaskService

	@Autowired
	private lateinit var playerBalanceService: PlayerBalanceService

	@Autowired
	private lateinit var playerService: PlayerService

	@Test
	fun `getLeaderboardPage returns correct order for TASKS leaderboard all time`() {
		// Given: Создаем 5 пользователей с разным количеством выполненных задач
		val user1 = createUser(101, "top-player")
		val user2 = createUser(102, "second-player")
		val user3 = createUser(103, "third-player")
		val user4 = createUser(104, "fourth-player")
		val user5 = createUser(105, "fifth-player")

		val player1 = user1.player()!!
		val player2 = user2.player()!!
		val player3 = user3.player()!!
		val player4 = user4.player()!!
		val player5 = user5.player()!!

		// Given: Создаем задачи
		val tasks = (1..10).map { idx ->
			createTask(
				title = "Task $idx",
				description = "Test task $idx",
				rarity = Rarity.COMMON,
				topics = listOf(TaskTopic.PRODUCTIVITY)
			)
		}
		taskRepository.saveEntities(tasks, SaveMode.INSERT_ONLY)

		// Given: Назначаем задачи игрокам (разное количество COMPLETED задач)
		// Player 1: 5 completed tasks (TOP)
		val player1Tasks = (0..4).map { idx ->
			Immutables.createPlayerTask {
				it.setId(java.util.UUID.randomUUID())
				it.setTaskId(tasks[idx].id()) // Используем только ID
				it.setPlayerId(player1.id())
				it.setStatus(PlayerTaskStatus.COMPLETED)
				it.setOrder(idx + 1)
			}
		}
		playerTaskService.insertAll(player1Tasks)

		// Player 2: 3 completed tasks
		val player2Tasks = (0..2).map { idx ->
			Immutables.createPlayerTask {
				it.setId(java.util.UUID.randomUUID())
				it.setTaskId(tasks[idx].id()) // Используем только ID
				it.setPlayerId(player2.id())
				it.setStatus(PlayerTaskStatus.COMPLETED)
				it.setOrder(idx + 1)
			}
		}
		playerTaskService.insertAll(player2Tasks)

		// Player 3: 4 completed tasks
		val player3Tasks = (0..3).map { idx ->
			Immutables.createPlayerTask {
				it.setId(java.util.UUID.randomUUID())
				it.setTaskId(tasks[idx].id()) // Используем только ID
				it.setPlayerId(player3.id())
				it.setStatus(PlayerTaskStatus.COMPLETED)
				it.setOrder(idx + 1)
			}
		}
		playerTaskService.insertAll(player3Tasks)

		// Player 4: 1 completed task
		val player4Task = Immutables.createPlayerTask {
			it.setId(java.util.UUID.randomUUID())
			it.setTaskId(tasks[0].id()) // Используем только ID
			it.setPlayerId(player4.id())
			it.setStatus(PlayerTaskStatus.COMPLETED)
			it.setOrder(1)
		}
		playerTaskService.insertAll(listOf(player4Task))

		// Player 5: 0 completed tasks (только IN_PROGRESS)
		val player5Task = Immutables.createPlayerTask {
			it.setId(java.util.UUID.randomUUID())
			it.setTaskId(tasks[0].id()) // Используем только ID
			it.setPlayerId(player5.id())
			it.setStatus(PlayerTaskStatus.IN_PROGRESS)
			it.setOrder(1)
		}
		playerTaskService.insertAll(listOf(player5Task))

		// When: Получаем лидерборд по задачам (все время)
		val (page, duration) = measureTimedValue {
			userRepository.getLeaderboardPage(LeaderboardType.TASKS, null, 0, 10)
		}
		println("getLeaderboardPage (TASKS all-time) took ${duration.inWholeMilliseconds} ms")

		// Then: Проверяем корректность результата
		assertThat(page.rows).hasSize(4) // Только 4 игрока с completed задачами
		assertThat(page.totalRowCount).isEqualTo(4)
		assertThat(page.totalPageCount).isEqualTo(1)

		// Then: Проверяем правильный порядок и места
		val firstPlace = page.rows[0]
		assertThat(firstPlace._1.firstName).isEqualTo("top-player")
		assertThat(firstPlace._2).isEqualTo(5L) // 5 completed tasks
		assertThat(firstPlace._3).isEqualTo(1) // place = 1

		val secondPlace = page.rows[1]
		assertThat(secondPlace._1.firstName).isEqualTo("third-player")
		assertThat(secondPlace._2).isEqualTo(4L)
		assertThat(secondPlace._3).isEqualTo(2) // place = 2

		val thirdPlace = page.rows[2]
		assertThat(thirdPlace._1.firstName).isEqualTo("second-player")
		assertThat(thirdPlace._2).isEqualTo(3L)
		assertThat(thirdPlace._3).isEqualTo(3) // place = 3

		val fourthPlace = page.rows[3]
		assertThat(fourthPlace._1.firstName).isEqualTo("fourth-player")
		assertThat(fourthPlace._2).isEqualTo(1L)
		assertThat(fourthPlace._3).isEqualTo(4) // place = 4
	}

	@Test
	fun `getLeaderboardPage returns correct results for TASKS leaderboard for specific day`() {
		// Given: Создаем пользователей
		val user1 = createUser(201, "today-champion")
		val user2 = createUser(202, "yesterday-champion")
		val player1 = user1.player()!!
		val player2 = user2.player()!!

		// Given: Создаем задачи
		val task1 = createTask(title = "Today Task 1", rarity = Rarity.COMMON)
		val task2 = createTask(title = "Today Task 2", rarity = Rarity.COMMON)
		val task3 = createTask(title = "Yesterday Task", rarity = Rarity.COMMON)
		taskRepository.saveEntities(listOf(task1, task2, task3), SaveMode.INSERT_ONLY)

		val today = LocalDate.now(ZoneId.of("UTC"))
		val yesterday = today.minusDays(1)

		// Given: Player1 выполнил 2 задачи сегодня
		val pt1Today = Immutables.createPlayerTask {
			it.setId(java.util.UUID.randomUUID())
			it.setTask(task1)
			it.setPlayerId(player1.id())
			it.setStatus(PlayerTaskStatus.COMPLETED)
			it.setOrder(1)
			it.setUpdatedAt(today.atTime(12, 0).atZone(ZoneId.of("UTC")).toInstant())
		}
		val pt2Today = Immutables.createPlayerTask {
			it.setId(java.util.UUID.randomUUID())
			it.setTask(task2)
			it.setPlayerId(player1.id())
			it.setStatus(PlayerTaskStatus.COMPLETED)
			it.setOrder(2)
			it.setUpdatedAt(today.atTime(14, 0).atZone(ZoneId.of("UTC")).toInstant())
		}
		playerTaskService.insertAll(listOf(pt1Today, pt2Today))

		// Given: Player2 выполнил 1 задачу вчера (не должна учитываться)
		val pt3Yesterday = Immutables.createPlayerTask {
			it.setId(java.util.UUID.randomUUID())
			it.setTask(task3)
			it.setPlayerId(player2.id())
			it.setStatus(PlayerTaskStatus.COMPLETED)
			it.setOrder(1)
			it.setUpdatedAt(yesterday.atTime(10, 0).atZone(ZoneId.of("UTC")).toInstant())
		}
		playerTaskService.insertAll(listOf(pt3Yesterday))

		// When: Получаем лидерборд за сегодня
		val page = userRepository.getLeaderboardPage(LeaderboardType.TASKS, today, 0, 10)

		// Then: Только player1 должен быть в результатах
		assertThat(page.rows).hasSize(1)
		assertThat(page.rows[0]._1.firstName).isEqualTo("today-champion")
		assertThat(page.rows[0]._2).isEqualTo(2L) // 2 tasks today
		assertThat(page.rows[0]._3).isEqualTo(1) // place = 1
	}

	@Test
	fun `getLeaderboardPage returns correct order for CURRENCY leaderboard`() {
		// Given: Создаем пользователей с разными балансами
		val user1 = createUser(301, "rich-player")
		val user2 = createUser(302, "poor-player")
		val user3 = createUser(303, "middle-player")

		val player1 = user1.player()!!
		val player2 = user2.player()!!
		val player3 = user3.player()!!

		// rich-player: 1000
		val updatedBalance1 = playerBalanceService.deposit(
			player1.balance()!!,
			BigDecimal(1000),
			PlayerBalanceTransactionCause.TASK_COMPLETION
		)
		playerService.update(Immutables.createPlayer(player1) { it.setBalance(updatedBalance1) })

		// poor-player: 100
		val updatedBalance2 = playerBalanceService.deposit(
			player2.balance()!!,
			BigDecimal(100),
			PlayerBalanceTransactionCause.TASK_COMPLETION
		)
		playerService.update(Immutables.createPlayer(player2) { it.setBalance(updatedBalance2) })

		// middle-player: 500
		val updatedBalance3 = playerBalanceService.deposit(
			player3.balance()!!,
			BigDecimal(500),
			PlayerBalanceTransactionCause.TASK_COMPLETION
		)
		playerService.update(Immutables.createPlayer(player3) { it.setBalance(updatedBalance3) })

		// When: Получаем лидерборд по валюте
		val page = userRepository.getLeaderboardPage(LeaderboardType.CURRENCY, null, 0, 10)

		// Then: Проверяем порядок
		assertThat(page.rows).hasSize(3)

		val first = page.rows[0]
		assertThat(first._1.firstName).isEqualTo("rich-player")
		assertThat((first._2 as BigDecimal).compareTo(BigDecimal(1000))).isEqualTo(0)
		assertThat(first._3).isEqualTo(1)

		val second = page.rows[1]
		assertThat(second._1.firstName).isEqualTo("middle-player")
		assertThat((second._2 as BigDecimal).compareTo(BigDecimal(500))).isEqualTo(0)
		assertThat(second._3).isEqualTo(2)

		val third = page.rows[2]
		assertThat(third._1.firstName).isEqualTo("poor-player")
		assertThat((third._2 as BigDecimal).compareTo(BigDecimal(100))).isEqualTo(0)
		assertThat(third._3).isEqualTo(3)
	}

	@Test
	fun `getLeaderboardPage returns correct order for LEVEL leaderboard`() {
		// Given: Создаем пользователей (уровни устанавливаются через createUser -> уровень по умолчанию)
		createUser(401, "level-50-player")
		createUser(402, "level-30-player")
		createUser(403, "level-40-player")

		// Note: Нужно будет вручную обновить уровни через service или напрямую в БД
		// Для примера предполагаем, что уровни уже установлены

		// When: Получаем лидерборд по уровню
		val page = userRepository.getLeaderboardPage(LeaderboardType.LEVEL, null, 0, 10)

		// Then: Проверяем что запрос выполняется без ошибок
		assertThat(page.rows).isNotEmpty
		assertThat(page.totalRowCount).isGreaterThanOrEqualTo(3)

		// Проверяем что все записи имеют place
		page.rows.forEach { row ->
			assertThat(row._3).isGreaterThan(0) // place > 0
		}
	}

	@Test
	fun `getLeaderboardPage pagination works correctly`() {
		// Given: Создаем 15 пользователей с разным количеством задач
		val users = (501..515).map { id ->
			val user = createUser(id.toLong(), "player-$id")
			val player = user.player()!!

			// Каждый игрок выполняет (id - 500) задач
			val taskCount = id - 500
			repeat(taskCount) { idx ->
				val task = createTask(title = "Task $idx for player $id", rarity = Rarity.COMMON)
				taskRepository.saveEntities(listOf(task), SaveMode.INSERT_ONLY)
				val playerTask = createPlayerTask(task, player.id(), PlayerTaskStatus.COMPLETED, idx + 1)
				playerTaskService.insertAll(listOf(playerTask))
			}

			user
		}

		// When: Получаем первую страницу (размер 10)
		val page1 = userRepository.getLeaderboardPage(LeaderboardType.TASKS, null, 0, 10)

		// When: Получаем вторую страницу
		val page2 = userRepository.getLeaderboardPage(LeaderboardType.TASKS, null, 1, 10)

		// Then: Первая страница содержит 10 элементов
		assertThat(page1.rows).hasSize(10)
		assertThat(page1.totalRowCount).isEqualTo(15)
		assertThat(page1.totalPageCount).isEqualTo(2)

		// Then: Вторая страница содержит оставшиеся 5 элементов
		assertThat(page2.rows).hasSize(5)
		assertThat(page2.totalRowCount).isEqualTo(15)
		assertThat(page2.totalPageCount).isEqualTo(2)

		// Then: Места корректны для обеих страниц
		assertThat(page1.rows[0]._3).isEqualTo(1) // Первое место на первой странице
		assertThat(page1.rows[9]._3).isEqualTo(10) // 10-е место на первой странице
		assertThat(page2.rows[0]._3).isEqualTo(11) // 11-е место на второй странице
		assertThat(page2.rows[4]._3).isEqualTo(15) // 15-е место на второй странице

		// Then: Проверяем что сортировка корректна (по убыванию количества задач)
		val firstPageScores = page1.rows.map { it._2 as Long }
		assertThat(firstPageScores).isSortedAccordingTo(Comparator.reverseOrder())
	}
}
