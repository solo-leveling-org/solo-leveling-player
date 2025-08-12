package com.sleepkqq.sololeveling.player.service.service.player.impl

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerBalanceTransaction
import com.sleepkqq.sololeveling.player.model.repository.player.PlayerBalanceTransactionRepository
import com.sleepkqq.sololeveling.player.service.service.player.PlayerBalanceTransactionService
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Suppress("unused")
@Service
class PlayerBalanceTransactionServiceImpl(
	private val playerBalanceTransactionRepository: PlayerBalanceTransactionRepository
) : PlayerBalanceTransactionService {

	@Transactional
	override fun insert(playerBalanceTransaction: PlayerBalanceTransaction): PlayerBalanceTransaction =
		playerBalanceTransactionRepository.save(playerBalanceTransaction, SaveMode.INSERT_ONLY)
}
