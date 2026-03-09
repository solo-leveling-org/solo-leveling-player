package com.soloist.player.service.gacha

import com.soloist.player.model.entity.player.dto.PlayerGearItemView
import java.math.BigDecimal

data class GachaResult(
	val obtainedItems: List<PlayerGearItemView>,
	val balanceAfter: BigDecimal
)
