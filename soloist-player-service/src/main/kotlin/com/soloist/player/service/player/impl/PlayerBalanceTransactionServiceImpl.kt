package com.soloist.player.service.player.impl

import com.soloist.player.model.entity.player.PlayerBalanceTransaction
import com.soloist.player.model.repository.player.PlayerBalanceTransactionRepository
import com.soloist.player.service.player.PlayerBalanceTransactionService
import com.soloist.proto.player.RequestPaging
import com.soloist.proto.player.RequestQueryOptions
import org.babyfish.jimmer.Page
import org.babyfish.jimmer.View
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.reflect.KClass

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
		paging: RequestPaging,
		viewType: KClass<V>
	): Page<V> = playerBalanceTransactionRepository.searchView(playerId, options, paging, viewType.java)
}
