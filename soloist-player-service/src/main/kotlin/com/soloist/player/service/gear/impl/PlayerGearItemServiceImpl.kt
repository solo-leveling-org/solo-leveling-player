package com.soloist.player.service.gear.impl

import com.soloist.player.model.entity.Immutables
import com.soloist.player.model.entity.gear.GearItem
import com.soloist.player.model.entity.gear.enums.GearItemTransactionType
import com.soloist.player.model.entity.player.PlayerGearItem
import com.soloist.player.model.entity.player.enums.PlayerGearItemStatus
import com.soloist.player.model.repository.player.PlayerGearItemRepository
import com.soloist.player.service.gear.GearItemTransactionService
import com.soloist.player.service.gear.PlayerGearItemService
import com.soloist.proto.common.RequestPaging
import com.soloist.proto.common.RequestQueryOptions
import org.babyfish.jimmer.Page
import org.babyfish.jimmer.View
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import kotlin.reflect.KClass

@Service
class PlayerGearItemServiceImpl(
	private val playerGearItemRepository: PlayerGearItemRepository,
	private val gearItemTransactionService: GearItemTransactionService
) : PlayerGearItemService {

	override fun initialize(
		inventoryId: UUID,
		gearItem: GearItem,
		transactionType: GearItemTransactionType,
		fromPlayerId: Long?,
		toPlayerId: Long?
	): PlayerGearItem {
		val transaction = gearItemTransactionService.initialize(
			type = transactionType,
			fromPlayerId = fromPlayerId,
			toPlayerId = toPlayerId
		)
		return Immutables.createPlayerGearItem {
			it.setId(UUID.randomUUID())
				.setInventoryId(inventoryId)
				.setGearItem(gearItem)
				.setStatus(PlayerGearItemStatus.IN_INVENTORY)
				.setTransactions(listOf(transaction))
		}
	}

	@Transactional(readOnly = true)
	override fun <V : View<PlayerGearItem>> searchView(
		playerId: Long,
		options: RequestQueryOptions,
		paging: RequestPaging,
		viewType: KClass<V>
	): Page<V> = playerGearItemRepository.searchView(playerId, options, paging, viewType.java)
}
