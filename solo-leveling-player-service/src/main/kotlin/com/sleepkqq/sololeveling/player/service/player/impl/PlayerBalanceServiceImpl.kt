package com.sleepkqq.sololeveling.player.service.player.impl

import com.sleepkqq.sololeveling.player.model.entity.Immutables
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerBalance
import com.sleepkqq.sololeveling.player.model.entity.player.enums.CurrencyCode
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerBalanceTransactionCause
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerBalanceTransactionType
import com.sleepkqq.sololeveling.player.model.repository.player.PlayerBalanceRepository
import com.sleepkqq.sololeveling.player.service.player.PlayerBalanceService
import com.sleepkqq.sololeveling.player.service.player.PlayerBalanceTransactionService
import org.babyfish.jimmer.View
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.UUID
import kotlin.reflect.KClass

@Service
class PlayerBalanceServiceImpl(
	private val playerBalanceTransactionService: PlayerBalanceTransactionService,
	private val playerBalanceRepository: PlayerBalanceRepository
) : PlayerBalanceService {

	private companion object {
		val INITIAL_BALANCE: BigDecimal = BigDecimal.ZERO
	}

	@Transactional(readOnly = true)
	override fun <V : View<PlayerBalance>> findView(id: Long, viewType: KClass<V>): V? =
		playerBalanceRepository.findView(id, viewType.java)

	override fun initializePlayerBalance(currencyCode: CurrencyCode): PlayerBalance =
		Immutables.createPlayerBalance {
			it.setId(UUID.randomUUID())
			it.setBalance(INITIAL_BALANCE)
			it.setCurrencyCode(currencyCode)
		}

	@Transactional
	override fun deposit(
		playerBalance: PlayerBalance,
		amount: BigDecimal,
		currencyCode: CurrencyCode,
		cause: PlayerBalanceTransactionCause
	): PlayerBalance {

		require(amount > BigDecimal.ZERO) {
			"Deposit amount=$amount cannot be negative or zero"
		}

		playerBalanceTransactionService.insert(
			Immutables.createPlayerBalanceTransaction {
				it.setAmount(amount)
				it.setCurrencyCode(currencyCode)
				it.setType(PlayerBalanceTransactionType.IN)
				it.setCause(cause)
				it.setBalanceId(playerBalance.id())
			}
		)

		val currentBalance = playerBalance.balance()

		return Immutables.createPlayerBalance(playerBalance) {
			it.setBalance(currentBalance.plus(amount))
		}
	}

	@Transactional
	override fun withdraw(
		playerBalance: PlayerBalance,
		amount: BigDecimal,
		cause: PlayerBalanceTransactionCause
	): PlayerBalance {

		require(amount > BigDecimal.ZERO) {
			"Withdraw amount=$amount cannot be negative or zero"
		}

		val currentBalance = playerBalance.balance()
		val newBalance = currentBalance.minus(amount)

		require(newBalance >= BigDecimal.ZERO) {
			"Insufficient funds for playerBalance=${playerBalance.id()}: " +
					"current balance is $currentBalance, requested amount is $amount"
		}

		playerBalanceTransactionService.insert(
			Immutables.createPlayerBalanceTransaction {
				it.setAmount(amount)
				it.setType(PlayerBalanceTransactionType.OUT)
				it.setCause(cause)
				it.setBalance(playerBalance)
			}
		)

		return Immutables.createPlayerBalance(playerBalance) {
			it.setBalance(newBalance)
		}
	}
}
