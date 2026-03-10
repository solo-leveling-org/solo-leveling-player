package com.soloist.player.service.player.impl

import com.soloist.player.model.entity.Immutables
import com.soloist.player.model.entity.player.Player
import com.soloist.player.model.entity.player.dto.ResetPlayerView
import com.soloist.player.model.entity.task.enums.DailyTaskType
import com.soloist.player.model.entity.player.enums.LevelType
import com.soloist.player.model.entity.task.enums.TaskTopic
import com.soloist.player.model.repository.player.PlayerRepository
import com.soloist.player.service.player.LevelService
import com.soloist.player.service.balance.BalanceService
import com.soloist.player.service.player.DailyTaskService
import com.soloist.player.service.player.DayStreakService
import com.soloist.player.service.player.PlayerService
import com.soloist.player.service.player.StaminaService
import com.soloist.player.service.gear.InventoryService
import com.soloist.player.service.task.PlayerTaskTopicService
import org.babyfish.jimmer.View
import org.babyfish.jimmer.sql.ast.mutation.AssociatedSaveMode
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.reflect.KClass

@Service
class PlayerServiceImpl(
	private val playerRepository: PlayerRepository,
	private val levelService: LevelService,
	private val balanceService: BalanceService,
	private val playerTaskTopicService: PlayerTaskTopicService,
	private val staminaService: StaminaService,
	private val dayStreakService: DayStreakService,
	private val dailyTaskService: DailyTaskService,
	private val inventoryService: InventoryService
) : PlayerService {

	@Transactional(readOnly = true)
	override fun <V : View<Player>> findView(id: Long, viewType: KClass<V>): V? =
		playerRepository.findView(id, viewType.java)

	@Transactional
	override fun insert(player: Player): Player =
		playerRepository.save(player, SaveMode.INSERT_ONLY)

	@Transactional
	override fun update(player: Player): Player =
		playerRepository.save(player, SaveMode.UPDATE_ONLY)

	override fun initialize(userId: Long): Player = Immutables.createPlayer {
		it.setId(userId)
			.setLevel(levelService.initialize(LevelType.PLAYER))
			.setBalance(balanceService.initialize())
			.setTaskTopics(
				TaskTopic.entries.map { topic ->
					playerTaskTopicService.initialize(topic)
				}
			)
			.setStamina(staminaService.initialize())
			.setDayStreak(dayStreakService.initialize())
			.setInventory(inventoryService.initialize())
			.setDailyTasks(
				DailyTaskType.entries.map { type ->
					dailyTaskService.initialize(userId, type)
				}
			)
	}

	@Transactional
	override fun reset(id: Long) {
		val player = getView(id, ResetPlayerView::class)

		val resetPlayer = Immutables.createPlayer(player.toEntity()) {
			it.setAgility(0)
				.setStrength(0)
				.setIntelligence(0)
				.setLevel(Immutables.createLevel(levelService.initialize(LevelType.PLAYER)) { l ->
					val level = player.level
					l.setId(level.id)
						.setVersion(level.version)
				})
				.setBalance(Immutables.createBalance(balanceService.initialize()) { b ->
					val balance = player.balance
					b.setId(balance.id)
						.setVersion(balance.version)
						.setTransactions(listOf())
				})
				.setTaskTopics(
					player.taskTopics.map { topic ->
						Immutables.createPlayerTaskTopic(topic.toEntity()) { t ->
							val level = topic.level

							t.setActive(false)
								.setLevel(Immutables.createLevel(levelService.initialize(LevelType.TASK_TOPIC)) { l ->
									l.setId(level.id)
										.setVersion(level.version)
								})
						}
					}
				)
				.setStamina(Immutables.createStamina(staminaService.initialize()) { s ->
					val stamina = player.stamina
					s.setId(stamina.id)
						.setVersion(stamina.version)
				})
				.setTasks(listOf())
		}

		playerRepository.save(resetPlayer, SaveMode.UPDATE_ONLY, AssociatedSaveMode.REPLACE)
	}
}
