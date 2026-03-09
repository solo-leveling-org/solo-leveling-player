package com.soloist.player.service.gear

import com.soloist.player.model.entity.player.dto.PlayerGearItemView
import org.babyfish.jimmer.Page

interface PlayerInventoryService {

	fun getInventory(playerId: Long, pageIndex: Int, pageSize: Int): Page<PlayerGearItemView>
}
