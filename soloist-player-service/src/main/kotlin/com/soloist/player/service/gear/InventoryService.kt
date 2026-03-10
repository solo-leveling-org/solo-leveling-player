package com.soloist.player.service.gear

import com.soloist.player.exception.ModelNotFoundException
import com.soloist.player.model.entity.player.Inventory
import org.babyfish.jimmer.View
import kotlin.reflect.KClass

interface InventoryService {

	fun initialize(): Inventory

	fun <V : View<Inventory>> findView(playerId: Long, viewType: KClass<V>): V?
	fun <V : View<Inventory>> getView(playerId: Long, viewType: KClass<V>): V =
		findView(playerId, viewType) ?: throw ModelNotFoundException(Inventory::class, playerId)
}
