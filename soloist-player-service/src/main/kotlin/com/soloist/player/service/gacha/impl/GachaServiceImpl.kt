package com.soloist.player.service.gacha.impl

import com.soloist.player.model.entity.balance.dto.BalanceWithPlayerView
import com.soloist.player.model.entity.balance.enums.BalanceTransactionCause
import com.soloist.player.model.entity.gacha.GachaMachine
import com.soloist.player.model.entity.gacha.dto.GachaMachineView
import com.soloist.player.model.entity.gacha.dto.GachaMachineWithItemsView
import com.soloist.player.model.entity.gear.enums.GearItemTransactionType
import com.soloist.player.model.entity.player.dto.InventoryView
import com.soloist.player.model.repository.gacha.GachaMachineRepository
import com.soloist.player.model.repository.player.PlayerGearItemRepository
import com.soloist.player.exception.ModelNotFoundException
import com.soloist.player.service.balance.BalanceService
import com.soloist.player.service.gacha.GachaItemDropService
import com.soloist.player.service.gacha.GachaResult
import com.soloist.player.service.gacha.GachaService
import com.soloist.player.service.gear.InventoryService
import com.soloist.player.service.gear.PlayerGearItemService
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class GachaServiceImpl(
	private val gachaMachineRepository: GachaMachineRepository,
	private val playerGearItemRepository: PlayerGearItemRepository,
	private val balanceService: BalanceService,
	private val gachaItemDropService: GachaItemDropService,
	private val inventoryService: InventoryService,
	private val playerGearItemService: PlayerGearItemService
) : GachaService {

	private val log = LoggerFactory.getLogger(javaClass)

	@Transactional(readOnly = true)
	override fun getActiveMachines(): List<GachaMachineView> =
		gachaMachineRepository.findAllActiveView(GachaMachineView::class.java)

	@Transactional
	override fun openGacha(playerId: Long, machineId: UUID, count: Int): GachaResult {
		require(count > 0) { "Pull count must be positive, got $count" }

		val machineWithItems =
			gachaMachineRepository.findView(machineId, GachaMachineWithItemsView::class.java)
				?.toEntity()
				?: throw ModelNotFoundException(GachaMachine::class, machineId)

		check(machineWithItems.isActive) { "Gacha machine $machineId is not active" }

		val totalCost = machineWithItems.costAmount().multiply(count.toBigDecimal())

		val balance = balanceService.getView(playerId, BalanceWithPlayerView::class)
			.toEntity()

		check(balance.amount() >= totalCost) {
			"Insufficient balance: required $totalCost but available ${balance.amount()}"
		}

		val updatedBalance = balanceService.withdraw(
			balance = balance,
			amount = totalCost,
			cause = BalanceTransactionCause.GACHA_PULL
		)

		val machineItems = machineWithItems.machineItems()

		check(machineItems.isNotEmpty()) {
			"Gacha machine $machineId has no items configured"
		}

		val droppedGearItems = gachaItemDropService.rollItems(machineItems, count)

		val inventory = inventoryService.getView(playerId, InventoryView::class)

		val currentGearItemCount = playerGearItemRepository.count(playerId)
		check(currentGearItemCount + count <= inventory.gearItemCapacity) {
			"Inventory full: $currentGearItemCount/${inventory.gearItemCapacity} gear items, cannot add $count more"
		}

		val playerGearItems = droppedGearItems.map {
			playerGearItemService.initialize(
				inventoryId = inventory.id,
				gearItem = it,
				transactionType = GearItemTransactionType.DROPPED,
				toPlayerId = playerId
			)
		}

		val savedItems = playerGearItemRepository.saveAll(playerGearItems, SaveMode.INSERT_ONLY)

		log.info(
			"Player {} opened gacha machine {}: pulled {} items, spent {}, balance after {}",
			playerId, machineId, count, totalCost, updatedBalance.amount()
		)

		return GachaResult(
			obtainedItems = savedItems,
			balanceAfter = updatedBalance.amount()
		)
	}
}
