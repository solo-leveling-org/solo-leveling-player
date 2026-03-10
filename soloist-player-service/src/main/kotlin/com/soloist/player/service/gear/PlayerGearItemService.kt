package com.soloist.player.service.gear

import com.soloist.player.model.entity.gear.GearItem
import com.soloist.player.model.entity.gear.enums.GearItemTransactionType
import com.soloist.player.model.entity.player.PlayerGearItem
import com.soloist.proto.common.RequestPaging
import com.soloist.proto.common.RequestQueryOptions
import org.babyfish.jimmer.Page
import org.babyfish.jimmer.View
import java.util.UUID
import kotlin.reflect.KClass

interface PlayerGearItemService {

	fun initialize(
		inventoryId: UUID,
		gearItem: GearItem,
		transactionType: GearItemTransactionType,
		fromPlayerId: Long? = null,
		toPlayerId: Long? = null
	): PlayerGearItem

	fun <V : View<PlayerGearItem>> searchView(
		playerId: Long,
		options: RequestQueryOptions,
		paging: RequestPaging,
		viewType: KClass<V>
	): Page<V>
}
