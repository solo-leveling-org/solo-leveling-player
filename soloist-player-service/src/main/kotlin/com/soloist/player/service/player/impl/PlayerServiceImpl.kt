package com.soloist.player.service.player.impl

import com.soloist.player.model.entity.Immutables
import com.soloist.player.model.entity.player.Player
import com.soloist.player.model.entity.player.dto.ResetPlayerView
import com.soloist.player.model.entity.player.enums.CurrencyCode
import com.soloist.player.model.repository.player.PlayerRepository
import com.soloist.player.service.balance.BalanceService
import com.soloist.player.service.player.DayStreakService
import com.soloist.player.service.player.PlayerService
import com.soloist.player.service.player.StaminaService
import org.babyfish.jimmer.View
import org.babyfish.jimmer.sql.ast.mutation.AssociatedSaveMode
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.reflect.KClass

@Service
class PlayerServiceImpl(
	private val playerRepository: PlayerRepository,
	private val balanceService: BalanceService,
	private val staminaService: StaminaService,
	private val dayStreakService: DayStreakService
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
			.setBalance(balanceService.initialize(CurrencyCode.GEM))
			.setStamina(staminaService.initialize())
			.setDayStreak(dayStreakService.initialize())
	}

	@Transactional
	override fun reset(id: Long) {
		val player = getView(id, ResetPlayerView::class)

		val resetPlayer = Immutables.createPlayer(player.toEntity()) {
			it.setBalance(Immutables.createBalance(balanceService.initialize(CurrencyCode.GEM)) { b ->
				val balance = player.balance
				b.setId(balance.id)
					.setVersion(balance.version)
					.setTransactions(listOf())
			})
				.setStamina(Immutables.createStamina(staminaService.initialize()) { s ->
					val stamina = player.stamina
					s.setId(stamina.id)
						.setVersion(stamina.version)
				})
		}

		playerRepository.save(resetPlayer, SaveMode.UPDATE_ONLY, AssociatedSaveMode.REPLACE)
	}
}
