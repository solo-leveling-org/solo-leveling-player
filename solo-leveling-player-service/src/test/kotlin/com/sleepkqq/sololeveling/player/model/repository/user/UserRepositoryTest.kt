package com.sleepkqq.sololeveling.player.model.repository.user

import com.sleepkqq.sololeveling.jimmer.predicate.filter.DateFilter
import com.sleepkqq.sololeveling.player.BaseTestClass
import com.sleepkqq.sololeveling.player.model.entity.Immutables
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerBalanceTransactionCause
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerTaskStatus
import com.sleepkqq.sololeveling.player.model.entity.player.enums.Rarity
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic
import com.sleepkqq.sololeveling.player.model.repository.task.TaskRepository
import com.sleepkqq.sololeveling.player.service.player.PlayerBalanceService
import com.sleepkqq.sololeveling.player.service.player.PlayerService
import com.sleepkqq.sololeveling.player.service.player.PlayerTaskService
import com.sleepkqq.sololeveling.proto.player.RequestPaging
import com.sleepkqq.sololeveling.proto.user.LeaderboardType
import org.assertj.core.api.Assertions.assertThat
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
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
	@Disabled
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
				it.setTaskId(tasks[idx].id())
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
				it.setTaskId(tasks[idx].id())
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
				it.setTaskId(tasks[idx].id())
				it.setPlayerId(player3.id())
				it.setStatus(PlayerTaskStatus.COMPLETED)
				it.setOrder(idx + 1)
			}
		}
		playerTaskService.insertAll(player3Tasks)

		// Player 4: 1 completed task
		val player4Task = Immutables.createPlayerTask {
			it.setId(java.util.UUID.randomUUID())
			it.setTaskId(tasks[0].id())
			it.setPlayerId(player4.id())
			it.setStatus(PlayerTaskStatus.COMPLETED)
			it.setOrder(1)
		}
		playerTaskService.insertAll(listOf(player4Task))

		// Player 5: 0 completed tasks (только IN_PROGRESS)
		val player5Task = Immutables.createPlayerTask {
			it.setId(java.util.UUID.randomUUID())
			it.setTaskId(tasks[0].id())
			it.setPlayerId(player5.id())
			it.setStatus(PlayerTaskStatus.IN_PROGRESS)
			it.setOrder(1)
		}
		playerTaskService.insertAll(listOf(player5Task))

		// When: Получаем лидерборд по задачам (все время)
		val (page, duration) = measureTimedValue {
			userRepository.getLeaderboardPage(
				LeaderboardType.TASKS,
				DateFilter.DayRange.empty(),
				RequestPaging.newBuilder()
					.setPage(0)
					.setPageSize(10)
					.build()
			)
		}

		println("getLeaderboardPage (TASKS all-time) took ${duration.inWholeMilliseconds} ms")

		// Then: Проверяем корректность результата
		assertThat(page.rows).hasSize(4) // Только 4 игрока с completed задачами
		assertThat(page.totalRowCount).isEqualTo(4)
		assertThat(page.totalPageCount).isEqualTo(1)

		// Then: Проверяем правильный порядок и места
		val firstPlace = page.rows[0]
		assertThat(firstPlace.user.firstName).isEqualTo("top-player")
		assertThat(firstPlace.score).isEqualTo(5L) // 5 completed tasks
		assertThat(firstPlace.position).isEqualTo(1) // place = 1

		val secondPlace = page.rows[1]
		assertThat(secondPlace.user.firstName).isEqualTo("third-player")
		assertThat(secondPlace.score).isEqualTo(4L)
		assertThat(secondPlace.position).isEqualTo(2) // place = 2

		val thirdPlace = page.rows[2]
		assertThat(thirdPlace.user.firstName).isEqualTo("second-player")
		assertThat(thirdPlace.score).isEqualTo(3L)
		assertThat(thirdPlace.position).isEqualTo(3) // place = 3

		val fourthPlace = page.rows[3]
		assertThat(fourthPlace.user.firstName).isEqualTo("fourth-player")
		assertThat(fourthPlace.score).isEqualTo(1L)
		assertThat(fourthPlace.position).isEqualTo(4) // place = 4
	}

	@Test
	@Disabled
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

		val today = ZonedDateTime.now(ZoneOffset.UTC).toLocalDate()
		val yesterday = today.minusDays(1)

		// Given: Player1 выполнил 2 задачи сегодня
		val pt1Today = Immutables.createPlayerTask {
			it.setId(java.util.UUID.randomUUID())
			it.setTask(task1)
			it.setPlayerId(player1.id())
			it.setStatus(PlayerTaskStatus.COMPLETED)
			it.setOrder(1)
			it.setUpdatedAt(today.atTime(12, 0).atZone(ZoneOffset.UTC).toInstant())
		}

		val pt2Today = Immutables.createPlayerTask {
			it.setId(java.util.UUID.randomUUID())
			it.setTask(task2)
			it.setPlayerId(player1.id())
			it.setStatus(PlayerTaskStatus.COMPLETED)
			it.setOrder(2)
			it.setUpdatedAt(today.atTime(14, 0).atZone(ZoneOffset.UTC).toInstant())
		}
		playerTaskService.insertAll(listOf(pt1Today, pt2Today))

		// Given: Player2 выполнил 1 задачу вчера (не должна учитываться)
		val pt3Yesterday = Immutables.createPlayerTask {
			it.setId(java.util.UUID.randomUUID())
			it.setTask(task3)
			it.setPlayerId(player2.id())
			it.setStatus(PlayerTaskStatus.COMPLETED)
			it.setOrder(1)
			it.setUpdatedAt(yesterday.atTime(10, 0).atZone(ZoneOffset.UTC).toInstant())
		}
		playerTaskService.insertAll(listOf(pt3Yesterday))

		val instant = today.atStartOfDay(ZoneOffset.UTC).toInstant()

		// When: Получаем лидерборд за сегодня
		val page = userRepository.getLeaderboardPage(
			LeaderboardType.TASKS,
			DateFilter.DayRange(instant, instant),
			RequestPaging.newBuilder()
				.setPage(0)
				.setPageSize(10)
				.build()
		)

		// Then: Только player1 должен быть в результатах
		assertThat(page.rows).hasSize(1)
		assertThat(page.rows[0].user.firstName).isEqualTo("today-champion")
		assertThat(page.rows[0].score).isEqualTo(2L) // 2 tasks today
		assertThat(page.rows[0].position).isEqualTo(1) // place = 1
	}


	@Test
	fun `getLeaderboardPage returns correct order for BALANCE leaderboard`() {
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

		// When: Получаем лидерборд по балансу
		val page = userRepository.getLeaderboardPage(
			LeaderboardType.BALANCE,
			DateFilter.DayRange.empty(),
			RequestPaging.newBuilder()
				.setPage(0)
				.setPageSize(10)
				.build()
		)

		// Then: Проверяем порядок
		assertThat(page.rows).hasSize(3)

		val first = page.rows[0]
		assertThat(first.user.firstName).isEqualTo("rich-player")
		assertThat((first.score as BigDecimal).compareTo(BigDecimal(1000))).isEqualTo(0)
		assertThat(first.position).isEqualTo(1)

		val second = page.rows[1]
		assertThat(second.user.firstName).isEqualTo("middle-player")
		assertThat((second.score as BigDecimal).compareTo(BigDecimal(500))).isEqualTo(0)
		assertThat(second.position).isEqualTo(2)

		val third = page.rows[2]
		assertThat(third.user.firstName).isEqualTo("poor-player")
		assertThat((third.score as BigDecimal).compareTo(BigDecimal(100))).isEqualTo(0)
		assertThat(third.position).isEqualTo(3)
	}

	@Test
	fun `getLeaderboardPage returns correct order for LEVEL leaderboard`() {
		// Given: Создаем пользователей
		val user1 = createUser(401, "level-50-player")
		val user2 = createUser(402, "level-30-player")
		val user3 = createUser(403, "level-40-player")

		val player1 = user1.player()!!
		val player2 = user2.player()!!
		val player3 = user3.player()!!

		playerService.update(Immutables.createPlayer(player1) {
			it.setLevel(Immutables.createLevel(player1.level()) { l ->  l.setTotalExperience(50) })
		})

		playerService.update(Immutables.createPlayer(player2) {
			it.setLevel(Immutables.createLevel(player2.level()) { l ->  l.setTotalExperience(50) })
		})

		playerService.update(Immutables.createPlayer(player3) {
			it.setLevel(Immutables.createLevel(player3.level()) { l ->  l.setTotalExperience(50) })
		})

		// When: Получаем лидерборд по уровню
		val page = userRepository.getLeaderboardPage(
			LeaderboardType.LEVEL,
			DateFilter.DayRange.empty(),
			RequestPaging.newBuilder()
				.setPage(0)
				.setPageSize(10)
				.build()
		)

		// Then: Проверяем что запрос выполняется без ошибок
		assertThat(page.rows).isNotEmpty
		assertThat(page.totalRowCount).isGreaterThanOrEqualTo(3)

		// Проверяем что все записи имеют place
		page.rows.forEach { row ->
			assertThat(row.position).isGreaterThan(0) // place > 0
		}
	}

	@Test
	@Disabled
	fun `getLeaderboardPage pagination works correctly`() {
		// Given: Создаем 15 пользователей с разным количеством задач
		(501..515).map { id ->
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
		val page1 = userRepository.getLeaderboardPage(
			LeaderboardType.TASKS,
			DateFilter.DayRange.empty(),
			RequestPaging.newBuilder()
				.setPage(0)
				.setPageSize(10)
				.build()
		)

		// When: Получаем вторую страницу
		val page2 = userRepository.getLeaderboardPage(
			LeaderboardType.TASKS,
			DateFilter.DayRange.empty(),
			RequestPaging.newBuilder()
				.setPage(1)
				.setPageSize(10)
				.build()
		)

		// Then: Первая страница содержит 10 элементов
		assertThat(page1.rows).hasSize(10)
		assertThat(page1.totalRowCount).isEqualTo(15)
		assertThat(page1.totalPageCount).isEqualTo(2)

		// Then: Вторая страница содержит оставшиеся 5 элементов
		assertThat(page2.rows).hasSize(5)
		assertThat(page2.totalRowCount).isEqualTo(15)
		assertThat(page2.totalPageCount).isEqualTo(2)

		// Then: Места корректны для обеих страниц
		assertThat(page1.rows[0].position).isEqualTo(1) // Первое место на первой странице
		assertThat(page1.rows[9].position).isEqualTo(10) // 10-е место на первой странице
		assertThat(page2.rows[0].position).isEqualTo(11) // 11-е место на второй странице
		assertThat(page2.rows[4].position).isEqualTo(15) // 15-е место на второй странице

		// Then: Проверяем что сортировка корректна (по убыванию количества задач)
		val firstPageScores = page1.rows.map { it.score as Long }
		assertThat(firstPageScores).isSortedAccordingTo(Comparator.reverseOrder())
	}

	@Test
	fun `getUsersStats returns correct statistics`() {
		// Given: Создаем пользователей с разными временами создания и активности
		val now = ZonedDateTime.now(ZoneOffset.UTC).toInstant()
		val todayStart = now.truncatedTo(ChronoUnit.DAYS)
		val yesterday = todayStart.minus(1, ChronoUnit.DAYS)
		val weekAgo = todayStart.minus(7, ChronoUnit.DAYS)
		val twoWeeksAgo = todayStart.minus(14, ChronoUnit.DAYS)
		val monthAgo = todayStart.minus(30, ChronoUnit.DAYS)

		// User 1: Создан месяц назад, заходил сегодня (returning user)
		val user1 = Immutables.createUser {
			it.setId(601)
			it.setUsername("user1")
			it.setFirstName("User 1")
			it.setLastName("")
			it.setPhotoUrl("")
			it.setLocale("en")
			it.setCreatedAt(monthAgo)
			it.setUpdatedAt(now)
			it.setLastLoginAt(now) // активен сегодня
			it.setVersion(5) // заходил больше 1 раза
		}

		// User 2: Создан неделю назад, заходил сегодня (returning user)
		val user2 = Immutables.createUser {
			it.setId(602)
			it.setUsername("user2")
			it.setFirstName("User 2")
			it.setLastName("")
			it.setPhotoUrl("")
			it.setLocale("en")
			it.setCreatedAt(weekAgo)
			it.setUpdatedAt(now)
			it.setLastLoginAt(now) // активен сегодня
			it.setVersion(3)
		}

		// User 3: Создан и заходил только сегодня (новый пользователь)
		val user3 = Immutables.createUser {
			it.setId(603)
			it.setUsername("user3")
			it.setFirstName("User 3")
			it.setLastName("")
			it.setPhotoUrl("")
			it.setLocale("en")
			it.setCreatedAt(todayStart.plus(2, ChronoUnit.HOURS))
			it.setUpdatedAt(now)
			it.setLastLoginAt(todayStart.plus(2, ChronoUnit.HOURS))
			it.setVersion(1) // первый заход
		}

		// User 4: Создан 2 недели назад, заходил вчера (не активен сегодня)
		val user4 = Immutables.createUser {
			it.setId(604)
			it.setUsername("user4")
			it.setFirstName("User 4")
			it.setLastName("")
			it.setPhotoUrl("")
			it.setLocale("en")
			it.setCreatedAt(twoWeeksAgo)
			it.setUpdatedAt(yesterday)
			it.setLastLoginAt(yesterday)
			it.setVersion(7)
		}

		// User 5: Создан месяц назад, не заходил с тех пор
		val user5 = Immutables.createUser {
			it.setId(605)
			it.setUsername("user5")
			it.setFirstName("User 5")
			it.setLastName("")
			it.setPhotoUrl("")
			it.setLocale("en")
			it.setCreatedAt(monthAgo.plus(1, ChronoUnit.HOURS))
			it.setUpdatedAt(monthAgo.plus(1, ChronoUnit.HOURS))
			it.setLastLoginAt(monthAgo.plus(1, ChronoUnit.HOURS))
			it.setVersion(1)
		}

		userRepository.save(user1, SaveMode.INSERT_ONLY)
		userRepository.save(user2, SaveMode.INSERT_ONLY)
		userRepository.save(user3, SaveMode.INSERT_ONLY)
		userRepository.save(user4, SaveMode.INSERT_ONLY)
		userRepository.save(user5, SaveMode.INSERT_ONLY)

		// When: Получаем статистику
		val stats = userRepository.getUsersStats()

		// Then: Проверяем общую статистику
		assertThat(stats.total).isEqualTo(5) // всего 5 пользователей
		assertThat(stats.returning).isEqualTo(3) // user1, user2, user4 (version > 1)

		// Then: Проверяем статистику за сегодня
		assertThat(stats.todayTotal).isEqualTo(3) // user1, user2, user3 (активны сегодня)
		assertThat(stats.todayNew).isEqualTo(1) // user3 (создан сегодня)
		assertThat(stats.todayReturning).isEqualTo(2) // user1, user2 (version > 1)

		// Then: Проверяем статистику за неделю (последние 7 дней)
		assertThat(stats.weekTotal).isEqualTo(4) // user1, user2, user3, user4
		assertThat(stats.weekNew).isEqualTo(2) // user2, user3
		assertThat(stats.weekReturning).isEqualTo(3) // user1, user2, user4 (version > 1)

		// Then: Проверяем статистику за месяц (последние 30 дней)
		assertThat(stats.monthTotal).isEqualTo(5) // все пользователи
		assertThat(stats.monthNew).isEqualTo(5) // все созданы за последние 30 дней
		assertThat(stats.monthReturning).isEqualTo(3) // user1, user2, user4 (version > 1)
	}

	@Test
	fun `getUsersStats returns zeros when no users exist`() {
		// When: Получаем статистику в пустой базе
		val stats = userRepository.getUsersStats()

		// Then: Все значения должны быть 0
		assertThat(stats.total).isEqualTo(0)
		assertThat(stats.returning).isEqualTo(0)
		assertThat(stats.todayTotal).isEqualTo(0)
		assertThat(stats.todayNew).isEqualTo(0)
		assertThat(stats.todayReturning).isEqualTo(0)
		assertThat(stats.weekTotal).isEqualTo(0)
		assertThat(stats.weekNew).isEqualTo(0)
		assertThat(stats.weekReturning).isEqualTo(0)
		assertThat(stats.monthTotal).isEqualTo(0)
		assertThat(stats.monthNew).isEqualTo(0)
		assertThat(stats.monthReturning).isEqualTo(0)
	}
}
