package com.sleepkqq.sololeveling.player.service.player

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerBalanceTransaction
import com.sleepkqq.sololeveling.proto.player.RequestPaging
import com.sleepkqq.sololeveling.proto.player.RequestQueryOptions
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
