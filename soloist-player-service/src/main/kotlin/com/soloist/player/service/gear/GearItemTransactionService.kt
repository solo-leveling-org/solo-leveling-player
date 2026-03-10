package com.soloist.player.service.gear

import com.soloist.player.model.entity.gear.GearItemTransaction
import com.soloist.player.model.entity.gear.enums.GearItemTransactionType

interface GearItemTransactionService {

	fun initialize(
		type: GearItemTransactionType,
		fromPlayerId: Long? = null,
		toPlayerId: Long? = null
	): GearItemTransaction
}
