package com.soloist.player.service.gear

import com.soloist.player.model.entity.gear.enums.ConsumableType
import com.soloist.player.model.entity.player.PlayerConsumable
import java.util.UUID

interface ConsumableService {

	fun initialize(inventoryId: UUID, type: ConsumableType, quantity: Int): PlayerConsumable

	fun add(playerId: Long, type: ConsumableType, quantity: Int)

	fun use(playerId: Long, type: ConsumableType)

	fun find(playerId: Long): List<PlayerConsumable>
}
