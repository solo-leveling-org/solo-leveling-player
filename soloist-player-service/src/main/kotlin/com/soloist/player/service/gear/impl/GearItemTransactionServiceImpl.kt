package com.soloist.player.service.gear.impl

import com.soloist.player.model.entity.Immutables
import com.soloist.player.model.entity.gear.GearItemTransaction
import com.soloist.player.model.entity.gear.enums.GearItemTransactionType
import com.soloist.player.service.gear.GearItemTransactionService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GearItemTransactionServiceImpl : GearItemTransactionService {

	override fun initialize(
		type: GearItemTransactionType,
		fromPlayerId: Long?,
		toPlayerId: Long?
	): GearItemTransaction = Immutables.createGearItemTransaction {
		it.setId(UUID.randomUUID())
			.setType(type)
		fromPlayerId?.let { id -> it.setFromPlayerId(id) }
		toPlayerId?.let { id -> it.setToPlayerId(id) }
	}
}
