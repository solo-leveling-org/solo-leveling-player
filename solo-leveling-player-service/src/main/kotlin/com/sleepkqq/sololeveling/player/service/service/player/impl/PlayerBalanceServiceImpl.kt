package com.sleepkqq.sololeveling.player.service.service.player.impl

import com.sleepkqq.sololeveling.player.model.entity.Immutables
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerBalance
import com.sleepkqq.sololeveling.player.model.entity.player.enums.CurrencyCode
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerBalanceTransactionCause
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerBalanceTransactionType
import com.sleepkqq.sololeveling.player.service.service.player.PlayerBalanceService
import com.sleepkqq.sololeveling.player.service.service.player.PlayerBalanceTransactionService
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Suppress("unused")
@Service
class PlayerBalanceServiceImpl(
	private val playerBalanceTransactionService: PlayerBalanceTransactionService
) : PlayerBalanceService {

	private companion object {
		val INITIAL_BALANCE: BigDecimal = BigDecimal.ZERO
	}

	override fun initializePlayerBalance(currencyCode: CurrencyCode): PlayerBalance =
		Immutables.createPlayerBalance {
			it.setId(UUID.randomUUID())
			it.setBalance(INITIAL_BALANCE)
			it.setCurrencyCode(currencyCode)
		}

	override fun deposit(
		playerBalance: PlayerBalance,
		amount: BigDecimal,
		currencyCode: CurrencyCode,
		cause: PlayerBalanceTransactionCause,
		now: LocalDateTime
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
				it.setCreatedAt(now)
				it.setUpdatedAt(now)
			}
		)

		val currentBalance = playerBalance.balance()

		return Immutables.createPlayerBalance(playerBalance) {
			it.setBalance(currentBalance.plus(amount))
			it.setUpdatedAt(now)
		}
	}

	override fun withdraw(
		playerBalance: PlayerBalance,
		amount: BigDecimal,
		cause: PlayerBalanceTransactionCause,
		now: LocalDateTime
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
				it.setCreatedAt(now)
				it.setUpdatedAt(now)
			}
		)

		return Immutables.createPlayerBalance(playerBalance) {
			it.setBalance(newBalance)
			it.setUpdatedAt(now)
		}
	}
}
