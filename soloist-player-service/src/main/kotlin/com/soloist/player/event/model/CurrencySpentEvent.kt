package com.soloist.player.event.model

import java.math.BigDecimal

data class CurrencySpentEvent(
	val playerId: Long,
	val amount: BigDecimal
)
