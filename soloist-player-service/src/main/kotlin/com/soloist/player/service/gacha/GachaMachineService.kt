package com.soloist.player.service.gacha

import com.soloist.player.model.entity.gacha.GachaMachine
import com.soloist.player.model.entity.gacha.dto.GachaMachineInput
import java.util.UUID

interface GachaMachineService {

	fun create(input: GachaMachineInput, imageFileId: String): GachaMachine

	fun addItem(machineId: UUID, gearItemId: UUID, weight: Int)

	fun removeItem(machineId: UUID, gearItemId: UUID)
}
