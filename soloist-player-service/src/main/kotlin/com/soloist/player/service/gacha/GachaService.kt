package com.soloist.player.service.gacha

import com.soloist.player.model.entity.gacha.dto.GachaMachineView
import java.util.UUID

interface GachaService {

	fun getActiveMachines(): List<GachaMachineView>

	fun openGacha(playerId: Long, machineId: UUID, count: Int): GachaResult
}
