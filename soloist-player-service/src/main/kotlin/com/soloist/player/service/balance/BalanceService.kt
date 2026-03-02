package com.soloist.player.service.balance

import com.soloist.player.model.entity.balance.Balance
import com.soloist.player.model.entity.player.enums.CurrencyCode
import com.soloist.player.model.entity.balance.enums.BalanceTransactionCause
import com.soloist.player.exception.ModelNotFoundException
import org.babyfish.jimmer.View
import java.math.BigDecimal
import kotlin.reflect.KClass

interface BalanceService {

	fun <V : View<Balance>> findView(playerId: Long, viewType: KClass<V>): V?
	fun <V : View<Balance>> getView(playerId: Long, viewType: KClass<V>): V = findView(playerId, viewType)
		?: throw ModelNotFoundException(Balance::class, playerId)

	fun initialize(currencyCode: CurrencyCode = CurrencyCode.SLCN): Balance
	fun deposit(
		balance: Balance,
		amount: BigDecimal,
		cause: BalanceTransactionCause,
		currencyCode: CurrencyCode = CurrencyCode.SLCN
	): Balance

	fun withdraw(
		balance: Balance,
		amount: BigDecimal,
		cause: BalanceTransactionCause
	): Balance
}
