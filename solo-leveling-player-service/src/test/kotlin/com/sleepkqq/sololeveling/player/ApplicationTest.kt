package com.sleepkqq.sololeveling.player

import com.sleepkqq.sololeveling.player.model.entity.Fetchers
import com.sleepkqq.sololeveling.player.model.entity.Immutables
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerTaskView
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerBalanceTransactionCause
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerBalanceTransactionType
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerTaskStatus
import com.sleepkqq.sololeveling.player.model.entity.player.enums.Rarity
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic
import com.sleepkqq.sololeveling.player.model.entity.user.enums.UserRole
import com.sleepkqq.sololeveling.player.model.repository.player.PlayerRepository
import com.sleepkqq.sololeveling.player.service.player.PlayerBalanceService
import com.sleepkqq.sololeveling.player.service.player.PlayerBalanceTransactionService
import com.sleepkqq.sololeveling.player.service.player.PlayerService
import com.sleepkqq.sololeveling.player.service.player.PlayerTaskService
import com.sleepkqq.sololeveling.player.service.user.UserService
import com.sleepkqq.sololeveling.proto.player.EnumFilter
import com.sleepkqq.sololeveling.proto.player.Filter
import com.sleepkqq.sololeveling.proto.player.RequestQueryOptions
import org.assertj.core.api.Assertions.assertThat
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal
import java.util.UUID

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
	private lateinit var playerTaskService: PlayerTaskService

	@Test
	fun `success deposit test`() {
		// Arrange
		val insertedUser = userService.upsert(
			Immutables.createUser {
				it.setId(1)
				it.setUsername("test")
				it.setFirstName("test")
				it.setLastName("test")
				it.setPhotoUrl("test")
				it.setLocale("test")
				it.setRoles(
					listOf(
						Immutables.createUserRoleItem { i -> i.setRole(UserRole.USER) }
					)
				)
			}
		)

		val player = insertedUser.player()!!

		val playerBalance = player.balance()!!

		playerBalanceTransactionService.insert(
			Immutables.createPlayerBalanceTransaction {
				it.setAmount(BigDecimal.TEN)
				it.setType(PlayerBalanceTransactionType.IN)
				it.setCause(PlayerBalanceTransactionCause.DAILY_CHECK_IN)
				it.setBalanceId(playerBalance.id())
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
			SaveMode.UPDATE_ONLY
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
		val insertedUser = userService.upsert(
			Immutables.createUser {
				it.setId(2)
				it.setUsername("test")
				it.setFirstName("test")
				it.setLastName("test")
				it.setPhotoUrl("test")
				it.setLocale("test")
				it.setRoles(
					listOf(
						Immutables.createUserRoleItem { i -> i.setRole(UserRole.USER) }
					)
				)
			}
		)

		val player = insertedUser.player()!!

		val task = Immutables.createTask {
			it.setId(UUID.randomUUID())
			it.setTitle(
				Immutables.createLocalizationItem { title ->
					title.setId(UUID.randomUUID())
					title.setRu("Название")
					title.setEn("Title")
				}
			)
			it.setDescription(
				Immutables.createLocalizationItem { description ->
					description.setId(UUID.randomUUID())
					description.setRu("Описание")
					description.setEn("Description")
				}
			)
			it.setExperience(100)
			it.setCurrencyReward(50)
			it.setRarity(Rarity.EPIC)
			it.setAgility(5)
			it.setStrength(10)
			it.setIntelligence(3)
			it.setTopics(
				listOf(
					Immutables.createTaskTopicItem { t ->
						t.setId(UUID.randomUUID())
						t.setTopic(TaskTopic.PRODUCTIVITY)
					},
					Immutables.createTaskTopicItem { t ->
						t.setId(UUID.randomUUID())
						t.setTopic(TaskTopic.HEALTHY_EATING)
					}
				)
			)
		}

		val playerTask = Immutables.createPlayerTask {
			it.setId(UUID.randomUUID())
			it.setTask(task)
			it.setPlayerId(player.id())
			it.setStatus(PlayerTaskStatus.COMPLETED)
			it.setOrder(1)
		}

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
}
