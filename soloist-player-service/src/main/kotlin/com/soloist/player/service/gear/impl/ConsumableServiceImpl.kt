package com.soloist.player.service.gear.impl

import com.soloist.player.model.entity.Immutables
import com.soloist.player.model.entity.gear.enums.ConsumableType
import com.soloist.player.model.entity.player.PlayerConsumable
import com.soloist.player.model.entity.player.dto.InventoryView
import com.soloist.player.model.repository.player.PlayerConsumableRepository
import com.soloist.player.service.gear.ConsumableService
import com.soloist.player.service.gear.InventoryService
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ConsumableServiceImpl(
	private val playerConsumableRepository: PlayerConsumableRepository,
	private val inventoryService: InventoryService
) : ConsumableService {

	@Transactional
	override fun add(playerId: Long, type: ConsumableType, quantity: Int) {
		require(quantity > 0) { "Quantity must be positive, got $quantity" }

		val inventory = inventoryService.getView(playerId, InventoryView::class)
		val currentTotalQuantity = playerConsumableRepository.sumQuantity(playerId)
		check(currentTotalQuantity + quantity <= inventory.consumableCapacity) {
			"Consumable capacity full: $currentTotalQuantity/${inventory.consumableCapacity}, cannot add $quantity more"
		}

		playerConsumableRepository.find(playerId, type)
			?.let { existing ->
				val updated = Immutables.createPlayerConsumable(existing) {
					it.setQuantity(existing.quantity() + quantity)
				}
				playerConsumableRepository.save(updated, SaveMode.UPDATE_ONLY)
			}
			?: run {
				val newConsumable = initialize(inventory.id, type, quantity)
				playerConsumableRepository.save(newConsumable, SaveMode.INSERT_ONLY)
			}
	}

	@Transactional
	override fun use(playerId: Long, type: ConsumableType) {
		val existing = playerConsumableRepository.find(playerId, type)
			?: throw IllegalStateException("Player $playerId does not have consumable $type")

		check(existing.quantity() > 0) { "Player $playerId has no $type left" }

		val updated = Immutables.createPlayerConsumable(existing) {
			it.setQuantity(existing.quantity() - 1)
		}
		playerConsumableRepository.save(updated, SaveMode.UPDATE_ONLY)
	}

	@Transactional(readOnly = true)
	override fun find(playerId: Long): List<PlayerConsumable> =
		playerConsumableRepository.find(playerId)

	override fun initialize(
		inventoryId: UUID,
		type: ConsumableType,
		quantity: Int
	): PlayerConsumable = Immutables.createPlayerConsumable {
		it.setId(UUID.randomUUID())
			.setInventoryId(inventoryId)
			.setConsumableType(type)
			.setQuantity(quantity)
	}
}
