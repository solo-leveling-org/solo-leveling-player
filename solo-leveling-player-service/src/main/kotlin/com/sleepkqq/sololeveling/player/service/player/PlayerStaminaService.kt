package com.sleepkqq.sololeveling.player.service.player

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerStamina

interface PlayerStaminaService {

	fun update(stamina: PlayerStamina): PlayerStamina
	fun initialize(): PlayerStamina
	fun calculateCurrent(stamina: PlayerStamina): PlayerStamina
	fun consume(stamina: PlayerStamina, amount: Int): PlayerStamina
	fun restore(stamina: PlayerStamina, amount: Int): PlayerStamina
	fun fullRestore(stamina: PlayerStamina): PlayerStamina
}
