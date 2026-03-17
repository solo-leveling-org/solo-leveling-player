package com.soloist.player.service.balance.impl

import com.soloist.player.event.model.CurrencySpentEvent
import com.soloist.player.model.entity.Immutables
import com.soloist.player.model.entity.balance.Balance
import com.soloist.player.model.entity.balance.enums.BalanceTransactionCause
import com.soloist.player.model.entity.balance.enums.BalanceTransactionType
import com.soloist.player.model.entity.player.enums.CurrencyCode
import com.soloist.player.model.repository.balance.BalanceRepository
import com.soloist.player.service.balance.BalanceService
import com.soloist.player.service.balance.BalanceTransactionService
import org.babyfish.jimmer.View
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.UUID
import kotlin.reflect.KClass

@Service
class BalanceServiceImpl(
	private val balanceTransactionService: BalanceTransactionService,
	private val balanceRepository: BalanceRepository,
	private val eventPublisher: ApplicationEventPublisher
) : BalanceService {

	@Transactional(readOnly = true)
	override fun <V : View<Balance>> findView(playerId: Long, viewType: KClass<V>): V? =
		balanceRepository.findView(playerId, viewType.java)

	override fun initialize(currencyCode: CurrencyCode): Balance =
		Immutables.createBalance {
			it.setId(UUID.randomUUID())
			it.setAmount(BigDecimal.ZERO)
			it.setCurrencyCode(currencyCode)
		}

	@Transactional
	override fun deposit(
		balance: Balance,
		amount: BigDecimal,
		cause: BalanceTransactionCause,
		currencyCode: CurrencyCode
	): Balance {

		require(amount > BigDecimal.ZERO) {
			"Deposit amount=$amount cannot be negative or zero"
		}

		balanceTransactionService.insert(
			Immutables.createBalanceTransaction {
				it.setAmount(amount)
				it.setCurrencyCode(currencyCode)
				it.setType(BalanceTransactionType.IN)
				it.setCause(cause)
				it.setBalanceId(balance.id())
			}
		)

		val updated = Immutables.createBalance(balance) {
			it.setAmount(balance.amount().plus(amount))
		}

		return balanceRepository.save(updated, SaveMode.UPDATE_ONLY)
	}

	@Transactional
	override fun withdraw(
		balance: Balance,
		amount: BigDecimal,
		cause: BalanceTransactionCause
	): Balance {

		require(amount > BigDecimal.ZERO) {
			"Withdraw amount=$amount cannot be negative or zero"
		}

		val currentBalance = balance.amount()
		val newBalance = currentBalance.minus(amount)

		require(newBalance >= BigDecimal.ZERO) {
			"Insufficient funds for playerBalance=${balance.id()}: " +
					"current balance is $currentBalance, requested amount is $amount"
		}

		balanceTransactionService.insert(
			Immutables.createBalanceTransaction {
				it.setAmount(amount)
				it.setType(BalanceTransactionType.OUT)
				it.setCause(cause)
				it.setBalance(balance)
			}
		)

		eventPublisher.publishEvent(CurrencySpentEvent(balance.player().id(), amount))

		val updated = Immutables.createBalance(balance) {
			it.setAmount(newBalance)
		}

		return balanceRepository.save(updated, SaveMode.UPDATE_ONLY)
	}
}
