package com.soloist.player.service.balance

import com.soloist.player.model.entity.balance.BalanceTransaction
import com.soloist.proto.common.RequestPaging
import com.soloist.proto.common.RequestQueryOptions
import org.babyfish.jimmer.Page
import org.babyfish.jimmer.View
import kotlin.reflect.KClass

interface BalanceTransactionService {

	fun insert(balanceTransaction: BalanceTransaction): BalanceTransaction

	fun <V : View<BalanceTransaction>> searchView(
		playerId: Long,
		options: RequestQueryOptions,
		paging: RequestPaging,
		viewType: KClass<V>
	) : Page<V>
}
