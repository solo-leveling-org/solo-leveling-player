package com.soloist.player.service.gacha.impl

import com.soloist.player.model.entity.gacha.GachaMachineItem
import com.soloist.player.model.entity.gear.GearItem
import com.soloist.player.service.gacha.GachaItemDropService
import org.springframework.stereotype.Service
import java.util.concurrent.ThreadLocalRandom

@Service
class GachaItemDropServiceImpl : GachaItemDropService {

	override fun rollItems(machineItems: List<GachaMachineItem>, count: Int): List<GearItem> {
		require(machineItems.isNotEmpty()) { "Cannot roll from an empty machine item pool" }
		require(count > 0) { "Roll count must be positive, got $count" }

		val totalWeight = machineItems.sumOf { it.weight() }

		return List(count) { rollSingle(machineItems, totalWeight) }
	}

	private fun rollSingle(machineItems: List<GachaMachineItem>, totalWeight: Int): GearItem {
		val roll = ThreadLocalRandom.current().nextInt(totalWeight)
		var accumulated = 0

		for (item in machineItems) {
			accumulated += item.weight()
			if (roll < accumulated) {
				return item.gearItem()
			}
		}

		// Fallback: should never happen if weights are valid, but return last item safely
		return machineItems.last().gearItem()
	}
}
