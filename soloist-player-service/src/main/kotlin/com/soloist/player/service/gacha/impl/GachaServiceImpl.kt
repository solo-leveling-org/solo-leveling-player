package com.soloist.player.service.gacha.impl

import com.soloist.player.model.entity.Immutables
import com.soloist.player.model.entity.balance.dto.BalanceWithPlayerView
import com.soloist.player.model.entity.gacha.GachaMachine
import com.soloist.player.model.entity.gacha.dto.GachaMachineView
import com.soloist.player.model.entity.gacha.dto.GachaMachineWithItemsView
import com.soloist.player.model.entity.player.dto.PlayerGearItemView
import com.soloist.player.model.entity.player.enums.PlayerGearItemStatus
import com.soloist.player.model.repository.gacha.GachaMachineRepository
import com.soloist.player.model.repository.player.PlayerGearItemRepository
import com.soloist.player.exception.ModelNotFoundException
import com.soloist.player.model.entity.balance.enums.BalanceTransactionCause
import com.soloist.player.service.balance.BalanceService
import com.soloist.player.service.gacha.GachaItemDropService
import com.soloist.player.service.gacha.GachaResult
import com.soloist.player.service.gacha.GachaService
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
	private val gachaItemDropService: GachaItemDropService
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

		val playerGearItems = droppedGearItems.map { gearItem ->
			Immutables.createPlayerGearItem {
				it.setPlayerId(playerId)
					.setGearItem(gearItem)
					.setStatus(PlayerGearItemStatus.IN_INVENTORY)
			}
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
