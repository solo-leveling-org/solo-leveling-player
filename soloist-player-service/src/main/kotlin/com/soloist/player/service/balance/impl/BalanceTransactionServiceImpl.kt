package com.soloist.player.service.balance.impl

import com.soloist.player.model.entity.balance.BalanceTransaction
import com.soloist.player.model.repository.balance.BalanceTransactionRepository
import com.soloist.player.service.balance.BalanceTransactionService
import com.soloist.proto.common.RequestPaging
import com.soloist.proto.common.RequestQueryOptions
import org.babyfish.jimmer.Page
import org.babyfish.jimmer.View
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.reflect.KClass

@Service
class BalanceTransactionServiceImpl(
	private val balanceTransactionRepository: BalanceTransactionRepository
) : BalanceTransactionService {

	@Transactional
	override fun insert(balanceTransaction: BalanceTransaction): BalanceTransaction =
		balanceTransactionRepository.save(balanceTransaction, SaveMode.INSERT_ONLY)

	@Transactional(readOnly = true)
	override fun <V : View<BalanceTransaction>> searchView(
		playerId: Long,
		options: RequestQueryOptions,
		paging: RequestPaging,
		viewType: KClass<V>
	): Page<V> = balanceTransactionRepository.searchView(playerId, options, paging, viewType.java)
}
