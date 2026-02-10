package com.sleepkqq.sololeveling.player.service.player

import com.sleepkqq.sololeveling.player.exception.ModelNotFoundException
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerStamina
import org.babyfish.jimmer.View
import kotlin.reflect.KClass

interface PlayerStaminaService {

	fun <V : View<PlayerStamina>> findView(playerId: Long, viewType: KClass<V>): V?
	fun <V : View<PlayerStamina>> getView(playerId: Long, viewType: KClass<V>): V =
		findView(playerId, viewType) ?: throw ModelNotFoundException(PlayerStamina::class, playerId)

	fun update(stamina: PlayerStamina): PlayerStamina
	fun initialize(): PlayerStamina
	fun calculateCurrent(stamina: PlayerStamina): PlayerStamina
	fun consume(stamina: PlayerStamina, amount: Int): PlayerStamina
	fun restore(stamina: PlayerStamina, amount: Int): PlayerStamina
	fun fullRestore(stamina: PlayerStamina): PlayerStamina
}
