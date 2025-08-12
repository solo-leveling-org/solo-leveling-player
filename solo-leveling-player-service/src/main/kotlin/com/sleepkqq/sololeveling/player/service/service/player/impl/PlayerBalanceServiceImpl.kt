package com.sleepkqq.sololeveling.player.service.service.player.impl

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerBalance
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerBalanceTransaction
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerBalanceTransactionCause
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerBalanceTransactionType
import com.sleepkqq.sololeveling.player.model.repository.player.PlayerBalanceRepository
import com.sleepkqq.sololeveling.player.service.service.player.PlayerBalanceService
import com.sleepkqq.sololeveling.player.service.service.player.PlayerBalanceTransactionService
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Suppress("unused")
@Service
class PlayerBalanceServiceImpl(
	private val playerBalanceRepository: PlayerBalanceRepository,
	private val playerBalanceTransactionService: PlayerBalanceTransactionService
) : PlayerBalanceService {

	private companion object {
		val INITIAL_BALANCE: BigDecimal = BigDecimal.ZERO
	}

	override fun initializePlayerBalance(): PlayerBalance = PlayerBalance {
		id = UUID.randomUUID()
		balance = INITIAL_BALANCE
	}

	override fun deposit(
		playerBalance: PlayerBalance,
		amount: BigDecimal,
		cause: PlayerBalanceTransactionCause,
		now: LocalDateTime
	): PlayerBalance {

		require(amount > BigDecimal.ZERO) {
			"Deposit amount=$amount cannot be negative or zero"
		}

		playerBalanceTransactionService.insert(
			PlayerBalanceTransaction {
				this.amount = amount
				type = PlayerBalanceTransactionType.IN
				this.cause = cause
				balanceId = playerBalance.id
				createdAt = now
				updatedAt = now
			}
		)

		val currentBalance = playerBalance.balance

		return PlayerBalance(playerBalance) {
			balance = currentBalance.plus(amount)
			updatedAt = now
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

		val currentBalance = playerBalance.balance
		val newBalance = currentBalance.minus(amount)

		require(newBalance >= BigDecimal.ZERO) {
			"Insufficient funds for playerBalance=${playerBalance.id}: " +
					"current balance is $currentBalance, requested amount is $amount"
		}

		playerBalanceTransactionService.insert(
			PlayerBalanceTransaction {
				this.amount = amount
				type = PlayerBalanceTransactionType.OUT
				this.cause = cause
				balance = playerBalance
				createdAt = now
				updatedAt = now
			}
		)

		return PlayerBalance(playerBalance) {
			balance = newBalance
			updatedAt = now
		}
	}
}
