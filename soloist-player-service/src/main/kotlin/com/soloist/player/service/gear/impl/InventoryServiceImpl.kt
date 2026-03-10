package com.soloist.player.service.gear.impl

import com.soloist.player.config.properties.InventoryProperties
import com.soloist.player.model.entity.Immutables
import com.soloist.player.model.entity.player.Inventory
import com.soloist.player.model.repository.player.InventoryRepository
import com.soloist.player.service.gear.InventoryService
import org.babyfish.jimmer.View
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import kotlin.reflect.KClass

@Service
class InventoryServiceImpl(
	private val inventoryRepository: InventoryRepository,
	private val inventoryProperties: InventoryProperties
) : InventoryService {

	override fun initialize(): Inventory = Immutables.createInventory {
		it.setId(UUID.randomUUID())
			.setGearItemCapacity(inventoryProperties.defaultGearItemCapacity)
			.setConsumableCapacity(inventoryProperties.defaultConsumableCapacity)
	}

	@Transactional(readOnly = true)
	override fun <V : View<Inventory>> findView(playerId: Long, viewType: KClass<V>): V? =
		inventoryRepository.findView(playerId, viewType.java)
}
