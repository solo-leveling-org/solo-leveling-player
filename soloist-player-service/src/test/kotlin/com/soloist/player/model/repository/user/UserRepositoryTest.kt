package com.soloist.player.model.repository.user

import com.soloist.jimmer.predicate.filter.DateFilter
import com.soloist.player.BaseTestClass
import com.soloist.player.model.entity.Immutables
import com.soloist.player.model.entity.balance.enums.BalanceTransactionCause
import com.soloist.player.service.balance.BalanceService
import com.soloist.player.service.player.PlayerService
import com.soloist.proto.common.LeaderboardType
import com.soloist.proto.common.RequestPaging
import org.assertj.core.api.Assertions.assertThat
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class UserRepositoryTest : BaseTestClass() {

	@Autowired
	private lateinit var userRepository: UserRepository

	@Autowired
	private lateinit var balanceService: BalanceService

	@Autowired
	private lateinit var playerService: PlayerService

	@Test
	fun `getLeaderboardPage returns correct order for BALANCE leaderboard`() {
		val user1 = createUser(301, "rich-player")
		val user2 = createUser(302, "poor-player")
		val user3 = createUser(303, "middle-player")

		val player1 = user1.player()!!
		val player2 = user2.player()!!
		val player3 = user3.player()!!

		balanceService.deposit(player1.balance()!!, BigDecimal(1000), BalanceTransactionCause.TASK_COMPLETION)
		balanceService.deposit(player2.balance()!!, BigDecimal(100), BalanceTransactionCause.TASK_COMPLETION)
		balanceService.deposit(player3.balance()!!, BigDecimal(500), BalanceTransactionCause.TASK_COMPLETION)

		val page = userRepository.getLeaderboardPage(
			LeaderboardType.BALANCE,
			DateFilter.DayRange.empty(),
			RequestPaging.newBuilder().setPage(0).setPageSize(10).build()
		)

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
	fun `getUsersStats returns correct statistics`() {
		val now = ZonedDateTime.now(ZoneOffset.UTC).toInstant()
		val todayStart = now.truncatedTo(ChronoUnit.DAYS)
		val yesterday = todayStart.minus(1, ChronoUnit.DAYS)
		val weekAgo = todayStart.minus(7, ChronoUnit.DAYS)
		val twoWeeksAgo = todayStart.minus(14, ChronoUnit.DAYS)
		val monthAgo = todayStart.minus(30, ChronoUnit.DAYS)

		val user1 = Immutables.createUser {
			it.setId(601).setUsername("user1").setFirstName("User 1").setLastName("")
				.setPhotoUrl("").setLocale("en").setCreatedAt(monthAgo).setUpdatedAt(now)
				.setLastLoginAt(now).setVersion(5)
		}
		val user2 = Immutables.createUser {
			it.setId(602).setUsername("user2").setFirstName("User 2").setLastName("")
				.setPhotoUrl("").setLocale("en").setCreatedAt(weekAgo).setUpdatedAt(now)
				.setLastLoginAt(now).setVersion(3)
		}
		val user3 = Immutables.createUser {
			it.setId(603).setUsername("user3").setFirstName("User 3").setLastName("")
				.setPhotoUrl("").setLocale("en")
				.setCreatedAt(todayStart.plus(2, ChronoUnit.HOURS)).setUpdatedAt(now)
				.setLastLoginAt(todayStart.plus(2, ChronoUnit.HOURS)).setVersion(0)
		}
		val user4 = Immutables.createUser {
			it.setId(604).setUsername("user4").setFirstName("User 4").setLastName("")
				.setPhotoUrl("").setLocale("en").setCreatedAt(twoWeeksAgo).setUpdatedAt(yesterday)
				.setLastLoginAt(yesterday).setVersion(7)
		}
		val user5 = Immutables.createUser {
			it.setId(605).setUsername("user5").setFirstName("User 5").setLastName("")
				.setPhotoUrl("").setLocale("en")
				.setCreatedAt(monthAgo.plus(1, ChronoUnit.HOURS))
				.setUpdatedAt(monthAgo.plus(1, ChronoUnit.HOURS))
				.setLastLoginAt(monthAgo.plus(1, ChronoUnit.HOURS)).setVersion(0)
		}

		userRepository.save(user1, SaveMode.INSERT_ONLY)
		userRepository.save(user2, SaveMode.INSERT_ONLY)
		userRepository.save(user3, SaveMode.INSERT_ONLY)
		userRepository.save(user4, SaveMode.INSERT_ONLY)
		userRepository.save(user5, SaveMode.INSERT_ONLY)

		val stats = userRepository.getUsersStats()

		assertThat(stats.total).isEqualTo(5)
		assertThat(stats.returning).isEqualTo(3)
		assertThat(stats.todayTotal).isEqualTo(3)
		assertThat(stats.todayNew).isEqualTo(1)
		assertThat(stats.todayReturning).isEqualTo(2)
		assertThat(stats.weekTotal).isEqualTo(4)
		assertThat(stats.weekNew).isEqualTo(2)
		assertThat(stats.weekReturning).isEqualTo(3)
		assertThat(stats.monthTotal).isEqualTo(5)
		assertThat(stats.monthNew).isEqualTo(5)
		assertThat(stats.monthReturning).isEqualTo(3)
	}

	@Test
	fun `getUsersStats returns zeros when no users exist`() {
		val stats = userRepository.getUsersStats()

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
