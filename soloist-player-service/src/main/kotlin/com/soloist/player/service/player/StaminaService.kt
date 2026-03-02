package com.soloist.player.service.player

import com.soloist.player.exception.ModelNotFoundException
import com.soloist.player.model.entity.player.Stamina
import org.babyfish.jimmer.View
import kotlin.reflect.KClass

interface StaminaService {

	fun <V : View<Stamina>> findView(playerId: Long, viewType: KClass<V>): V?
	fun <V : View<Stamina>> getView(playerId: Long, viewType: KClass<V>): V =
		findView(playerId, viewType) ?: throw ModelNotFoundException(Stamina::class, playerId)

	fun update(stamina: Stamina): Stamina
	fun initialize(): Stamina
	fun calculateCurrent(stamina: Stamina): Stamina
	fun consume(stamina: Stamina, amount: Int): Stamina
	fun restore(stamina: Stamina, amount: Int): Stamina
	fun fullRestore(stamina: Stamina): Stamina
}
