package com.soloist.player.service.player

import com.soloist.player.model.entity.player.PlayerBalanceTransaction
import com.soloist.proto.player.RequestPaging
import com.soloist.proto.player.RequestQueryOptions
import org.babyfish.jimmer.Page
import org.babyfish.jimmer.View
import kotlin.reflect.KClass

interface PlayerBalanceTransactionService {

	fun insert(playerBalanceTransaction: PlayerBalanceTransaction): PlayerBalanceTransaction

	fun <V : View<PlayerBalanceTransaction>> searchView(
		playerId: Long,
		options: RequestQueryOptions,
		paging: RequestPaging,
		viewType: KClass<V>
	) : Page<V>
}
