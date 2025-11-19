package com.sleepkqq.sololeveling.player.service.player

import com.sleepkqq.sololeveling.player.BaseTestClass
import com.sleepkqq.sololeveling.player.model.entity.Fetchers
import com.sleepkqq.sololeveling.player.model.entity.Immutables
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerBalanceTransactionCause
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal

class PlayerBalanceServiceTest : BaseTestClass() {

	@Autowired
	private lateinit var playerService: PlayerService

	@Autowired
	private lateinit var playerBalanceService: PlayerBalanceService

	@Test
	fun `success deposit test`() {
		// Arrange
		val user = createUser(1, "deposit-test")
		val player = user.player()!!
		val playerBalance = player.balance()!!
		createPlayerBalanceTransaction(playerBalance.id())

		val dbPlayer = playerService.get(
			player.id(),
			Fetchers.PLAYER_FETCHER
				.allScalarFields()
				.balance(
					Fetchers.PLAYER_BALANCE_FETCHER
						.allScalarFields()
				)
		)

		val updatedBalance = playerBalanceService.deposit(
			dbPlayer.balance()!!,
			BigDecimal.TWO,
			PlayerBalanceTransactionCause.TASK_COMPLETION
		)

		playerService.update(
			Immutables.createPlayer(dbPlayer) {
				it.setBalance(updatedBalance)
			}
		)

		val playerWithTransactions = playerService.get(
			player.id(),
			Fetchers.PLAYER_FETCHER
				.version()
				.balance(
					Fetchers.PLAYER_BALANCE_FETCHER
						.allScalarFields()
						.transactions(
							Fetchers.PLAYER_BALANCE_TRANSACTION_FETCHER
								.allScalarFields()
						)
				)
		)

		assertThat(playerWithTransactions.version()).isEqualTo(1)
		assertThat(playerWithTransactions.balance()!!.version()).isEqualTo(1)
		assertThat(playerWithTransactions.balance()!!.balance().compareTo(BigDecimal.TWO)).isEqualTo(0)
		assertThat(playerWithTransactions.balance()!!.transactions().size).isEqualTo(2)
	}
}
