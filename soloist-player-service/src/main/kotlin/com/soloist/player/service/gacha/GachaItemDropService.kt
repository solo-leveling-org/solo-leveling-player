package com.soloist.player.service.gacha

import com.soloist.player.model.entity.gacha.GachaMachineItem
import com.soloist.player.model.entity.gear.GearItem

interface GachaItemDropService {

	fun rollItems(machineItems: List<GachaMachineItem>, count: Int): List<GearItem>
}
