package com.sleepkqq.sololeveling.player.service.player

import com.sleepkqq.sololeveling.player.exception.ModelNotFoundException
import com.sleepkqq.sololeveling.player.model.entity.Fetchers
import com.sleepkqq.sololeveling.player.model.entity.player.Player
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerStamina
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerStaminaFetcher
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerStaminaView
import com.sleepkqq.sololeveling.player.model.entity.player.enums.Rarity

interface PlayerStaminaService {

	fun find(
		playerId: Long,
		fetcher: PlayerStaminaFetcher = Fetchers.PLAYER_STAMINA_FETCHER.allScalarFields()
	): PlayerStamina?

	fun get(
		playerId: Long,
		fetcher: PlayerStaminaFetcher = Fetchers.PLAYER_STAMINA_FETCHER.allScalarFields()
	): PlayerStamina = find(playerId, fetcher)
		?: throw ModelNotFoundException(Player::class, playerId)

	fun update(stamina: PlayerStamina): PlayerStamina
	fun initialize(playerId: Long): PlayerStamina
	fun calculateCurrentStamina(stamina: PlayerStamina): PlayerStamina
	fun getCurrentStamina(playerId: Long): PlayerStaminaView
	fun consumeStamina(stamina: PlayerStamina, rarity: Rarity): PlayerStamina
	fun restoreStamina(stamina: PlayerStamina, amount: Int): PlayerStamina
	fun fullRestore(stamina: PlayerStamina): PlayerStamina
}
