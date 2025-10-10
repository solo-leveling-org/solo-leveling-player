package com.sleepkqq.sololeveling.player.service.service.player

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerBalance
import com.sleepkqq.sololeveling.player.model.entity.player.enums.CurrencyCode
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerBalanceTransactionCause
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
interface PlayerBalanceService {

	fun initializePlayerBalance(currencyCode: CurrencyCode = CurrencyCode.SLCN): PlayerBalance
	fun deposit(
		playerBalance: PlayerBalance,
		amount: BigDecimal,
		currencyCode: CurrencyCode = CurrencyCode.SLCN,
		cause: PlayerBalanceTransactionCause,
		now: LocalDateTime = LocalDateTime.now()
	): PlayerBalance

	fun withdraw(
		playerBalance: PlayerBalance,
		amount: BigDecimal,
		cause: PlayerBalanceTransactionCause,
		now: LocalDateTime = LocalDateTime.now()
	): PlayerBalance
}
