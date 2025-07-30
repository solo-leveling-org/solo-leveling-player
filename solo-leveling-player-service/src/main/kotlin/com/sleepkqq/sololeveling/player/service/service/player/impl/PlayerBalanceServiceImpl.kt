package com.sleepkqq.sololeveling.player.service.service.player.impl

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerBalance
import com.sleepkqq.sololeveling.player.service.service.player.PlayerBalanceService
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.UUID

@Suppress("unused")
@Service
class PlayerBalanceServiceImpl : PlayerBalanceService {

	private companion object {
		val INITIAL_BALANCE: BigDecimal = BigDecimal.ZERO
	}

	override fun initializePlayerBalance(): PlayerBalance = PlayerBalance {
		id = UUID.randomUUID()
		balance = INITIAL_BALANCE
	}
}