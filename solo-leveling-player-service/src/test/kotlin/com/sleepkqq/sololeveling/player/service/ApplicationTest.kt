package com.sleepkqq.sololeveling.player.service

import com.sleepkqq.sololeveling.player.model.entity.Fetchers
import com.sleepkqq.sololeveling.player.model.entity.Immutables
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerBalanceTransactionCause
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerBalanceTransactionType
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskRarity
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic
import com.sleepkqq.sololeveling.player.model.entity.user.enums.UserRole
import com.sleepkqq.sololeveling.player.model.repository.player.PlayerRepository
import com.sleepkqq.sololeveling.player.service.service.player.PlayerBalanceService
import com.sleepkqq.sololeveling.player.service.service.player.PlayerBalanceTransactionService
import com.sleepkqq.sololeveling.player.service.service.player.PlayerService
import com.sleepkqq.sololeveling.player.service.service.task.TaskService
import com.sleepkqq.sololeveling.player.service.service.user.UserService
import org.assertj.core.api.Assertions.assertThat
import org.babyfish.jimmer.sql.ast.mutation.AssociatedSaveMode
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal
import java.time.LocalDateTime

class ApplicationTest : BaseTestClass() {

	@Autowired
	private lateinit var userService: UserService

	@Autowired
	private lateinit var playerService: PlayerService

	@Autowired
	private lateinit var playerRepository: PlayerRepository

	@Autowired
	private lateinit var playerBalanceService: PlayerBalanceService

	@Autowired
	private lateinit var playerBalanceTransactionService: PlayerBalanceTransactionService

	@Autowired
	private lateinit var taskService: TaskService

	@Test
	fun `insert should save task to database`() {
		// Arrange
		val now = LocalDateTime.now()
		val initialTask = Immutables.createTask {
			it.setTitle("Test Task Title")
			it.setDescription("Test Task Description")
			it.setExperience(100)
			it.setCurrencyReward(50)
			it.setRarity(TaskRarity.EPIC)
			it.setAgility(5)
			it.setStrength(10)
			it.setIntelligence(3)
			it.setTopics(setOf(TaskTopic.PRODUCTIVITY, TaskTopic.SOCIAL_SKILLS))
			it.setCreatedAt(now)
			it.setUpdatedAt(now)
		}

		// Act
		val savedTask = taskService.insert(initialTask)

		val newAgilityValue = 7
		taskService.update(
			Immutables.createTask {
				it.setId(savedTask.id())
				it.setAgility(newAgilityValue)
				it.setVersion(savedTask.version())
			}
		)

		val updatedTask = taskService.get(savedTask.id())

		// Assert
		assertThat(updatedTask.version()).isEqualTo(savedTask.version() + 1)
		assertThat(updatedTask.agility()).isEqualTo(newAgilityValue)
		assertThat(updatedTask.createdAt()).isNotNull()
	}

	@Test
	fun `success deposit test`() {
		// Arrange
		val now = LocalDateTime.now()

		val insertedUser = userService.upsert(
			Immutables.createUser {
				it.setId(1)
				it.setUsername("test")
				it.setFirstName("test")
				it.setLastName("test")
				it.setPhotoUrl("test")
				it.setLocale("test")
				it.setLastLoginAt(now)
				it.setUpdatedAt(now)
				it.setCreatedAt(now)
				it.setRoles(listOf(UserRole.USER))
			}
		)

		val player = insertedUser.player()!!

		val playerBalance = player.balance()!!

		playerBalanceTransactionService.insert(
			Immutables.createPlayerBalanceTransaction {
				it.setAmount(BigDecimal.TEN)
				it.setType(PlayerBalanceTransactionType.IN)
				it.setCause(PlayerBalanceTransactionCause.DAILY_CHECK_IN)
				it.setBalance(playerBalance)
			}
		)

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

		playerRepository.save(
			Immutables.createPlayer(dbPlayer) {
				it.setBalance(updatedBalance)
			},
			SaveMode.UPDATE_ONLY,
			AssociatedSaveMode.MERGE
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
}
