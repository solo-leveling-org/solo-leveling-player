package com.sleepkqq.sololeveling.player.service

import com.sleepkqq.sololeveling.player.model.entity.player.Player
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerBalanceTransaction
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerBalanceTransactionCause
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerBalanceTransactionType
import com.sleepkqq.sololeveling.player.model.entity.task.Task
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskRarity
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic
import com.sleepkqq.sololeveling.player.model.entity.user.User
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
		val initialTask = Task {
			this.title = "Test Task Title"
			this.description = "Test Task Description"
			this.experience = 100
			this.currencyReward = 50
			this.rarity = TaskRarity.EPIC
			this.agility = 5
			this.strength = 10
			this.intelligence = 3
			this.topics = setOf(TaskTopic.PRODUCTIVITY, TaskTopic.SOCIAL_SKILLS)
			createdAt = now
			updatedAt = now
		}

		// Act
		val savedTask = taskService.insert(initialTask)

		val newAgilityValue = 7
		taskService.update(Task {
			id = savedTask.id
			agility = newAgilityValue
			version = savedTask.version
		})

		val updatedTask = taskService.get(savedTask.id)

		// Assert
		assertThat(updatedTask.version).isEqualTo(savedTask.version + 1)
		assertThat(updatedTask.agility).isEqualTo(newAgilityValue)
		assertThat(updatedTask.createdAt).isNotNull()
	}

	@Test
	fun `success deposit test`() {
		// Arrange
		val now = LocalDateTime.now()

		val insertedUser = userService.upsert(
			User {
				id = 1
				username = "test"
				firstName = "test"
				lastName = "test"
				photoUrl = "test"
				locale = "test"
				lastLoginAt = now
				updatedAt = now
				createdAt = now
				roles = listOf(UserRole.USER)
			}
		)

		val player = insertedUser.player!!

		val playerBalance = player.balance!!

		playerBalanceTransactionService.insert(
			PlayerBalanceTransaction {
				amount = BigDecimal.TEN
				type = PlayerBalanceTransactionType.IN
				cause = PlayerBalanceTransactionCause.DAILY_CHECK_IN
				balance = playerBalance
			}
		)

		val dbPlayer = playerService.get(player.id) {
			allScalarFields()
			balance { allScalarFields() }
		}

		val updatedBalance = playerBalanceService.deposit(
			dbPlayer.balance!!,
			BigDecimal.TWO,
			PlayerBalanceTransactionCause.TASK_COMPLETION
		)

		playerRepository.save(
			Player(dbPlayer) {
				balance = updatedBalance
			},
			SaveMode.UPDATE_ONLY,
			AssociatedSaveMode.MERGE
		)

		val playerWithTransactions = playerService.get(player.id) {
			version()
			balance {
				allScalarFields()
				transactions { allScalarFields() }
			}
		}

		assertThat(playerWithTransactions.version).isEqualTo(1)
		assertThat(playerWithTransactions.balance!!.version).isEqualTo(1)
		assertThat(playerWithTransactions.balance!!.balance.compareTo(BigDecimal.TWO)).isEqualTo(0)
		assertThat(playerWithTransactions.balance!!.transactions.size).isEqualTo(2)
	}
}
