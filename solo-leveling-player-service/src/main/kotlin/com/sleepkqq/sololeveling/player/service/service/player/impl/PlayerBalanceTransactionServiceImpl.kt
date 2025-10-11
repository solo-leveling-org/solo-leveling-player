package com.sleepkqq.sololeveling.player.service.service.player.impl

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerBalanceTransaction
import com.sleepkqq.sololeveling.player.model.repository.player.PlayerBalanceTransactionRepository
import com.sleepkqq.sololeveling.player.service.service.player.PlayerBalanceTransactionService
import com.sleepkqq.sololeveling.proto.player.RequestQueryOptions
import org.babyfish.jimmer.Page
import org.babyfish.jimmer.View
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.reflect.KClass

@Suppress("unused")
@Service
class PlayerBalanceTransactionServiceImpl(
	private val playerBalanceTransactionRepository: PlayerBalanceTransactionRepository
) : PlayerBalanceTransactionService {

	@Transactional
	override fun insert(playerBalanceTransaction: PlayerBalanceTransaction): PlayerBalanceTransaction =
		playerBalanceTransactionRepository.save(playerBalanceTransaction, SaveMode.INSERT_ONLY)

	@Transactional(readOnly = true)
	override fun <V : View<PlayerBalanceTransaction>> searchView(
		playerId: Long,
		options: RequestQueryOptions,
		viewType: KClass<V>
	): Page<V> = playerBalanceTransactionRepository.searchView(playerId, options, viewType.java)
}
