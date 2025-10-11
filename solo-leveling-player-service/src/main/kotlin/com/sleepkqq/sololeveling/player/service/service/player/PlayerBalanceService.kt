package com.sleepkqq.sololeveling.player.service.service.player

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerBalance
import com.sleepkqq.sololeveling.player.model.entity.player.enums.CurrencyCode
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerBalanceTransactionCause
import com.sleepkqq.sololeveling.player.service.exception.ModelNotFoundException
import org.babyfish.jimmer.View
import java.math.BigDecimal
import java.time.LocalDateTime
import kotlin.reflect.KClass

interface PlayerBalanceService {

	fun <V : View<PlayerBalance>> findView(id: Long, viewType: KClass<V>): V?
	fun <V : View<PlayerBalance>> getView(id: Long, viewType: KClass<V>): V = findView(id, viewType)
		?: throw ModelNotFoundException(PlayerBalance::class, id)

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
