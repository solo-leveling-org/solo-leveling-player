package com.sleepkqq.sololeveling.player.service.player.impl

import com.sleepkqq.sololeveling.player.model.entity.Fetchers
import com.sleepkqq.sololeveling.player.model.entity.Immutables
import com.sleepkqq.sololeveling.player.model.entity.player.Player
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerFetcher
import com.sleepkqq.sololeveling.player.model.entity.player.enums.LevelType
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic
import com.sleepkqq.sololeveling.player.model.repository.player.PlayerRepository
import com.sleepkqq.sololeveling.player.service.player.LevelService
import com.sleepkqq.sololeveling.player.service.player.PlayerBalanceService
import com.sleepkqq.sololeveling.player.service.player.PlayerService
import com.sleepkqq.sololeveling.player.service.player.PlayerStaminaService
import com.sleepkqq.sololeveling.player.service.player.PlayerTaskTopicService
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
	private val playerBalanceService: PlayerBalanceService,
	private val playerTaskTopicService: PlayerTaskTopicService,
	private val playerStaminaService: PlayerStaminaService
) : PlayerService {

	@Transactional(readOnly = true)
	override fun find(id: Long, fetcher: PlayerFetcher): Player? =
		playerRepository.findNullable(id, fetcher)

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
			.setBalance(playerBalanceService.initialize())
			.setTaskTopics(
				TaskTopic.entries.map { topic ->
					playerTaskTopicService.initialize(topic)
				}
			)
			.setStamina(playerStaminaService.initialize())
	}

	@Transactional
	override fun reset(id: Long) {
		val player = get(
			id,
			Fetchers.PLAYER_FETCHER.allScalarFields()
				.level(Fetchers.LEVEL_FETCHER.allScalarFields())
				.balance(Fetchers.PLAYER_BALANCE_FETCHER.allScalarFields())
				.taskTopics(
					Fetchers.PLAYER_TASK_TOPIC_FETCHER.allScalarFields()
						.level(Fetchers.LEVEL_FETCHER.allScalarFields())
				)
				.stamina(Fetchers.PLAYER_STAMINA_FETCHER.allScalarFields())
		)

		val resetPlayer = Immutables.createPlayer(player) {
			it.setAgility(0)
				.setStrength(0)
				.setIntelligence(0)
				.setLevel(Immutables.createLevel(levelService.initialize(LevelType.PLAYER)) { l ->
					val level = player.level()!!
					l.setId(level.id())
						.setVersion(level.version())
				})
				.setBalance(Immutables.createPlayerBalance(playerBalanceService.initialize()) { b ->
					val balance = player.balance()!!
					b.setId(balance.id())
						.setVersion(balance.version())
						.setTransactions(listOf())
				})
				.setTaskTopics(
					player.taskTopics().map { topic ->
						Immutables.createPlayerTaskTopic(topic) { t ->
							val level = t.level()!!

							t.setActive(false)
								.setLevel(Immutables.createLevel(levelService.initialize(LevelType.TASK_TOPIC)) { l ->
									l.setId(level.id())
										.setVersion(level.version())
								})
						}
					}
				)
				.setStamina(Immutables.createPlayerStamina(playerStaminaService.initialize()) { s ->
					val stamina = player.stamina()!!
					s.setId(stamina.id())
						.setVersion(stamina.version())
				})
				.setTasks(listOf())
		}

		playerRepository.save(resetPlayer, SaveMode.UPDATE_ONLY, AssociatedSaveMode.REPLACE)
	}
}
