package com.soloist.player.service.gear

import com.soloist.player.model.entity.gear.GearItem
import com.soloist.player.model.entity.gear.dto.GearItemInput
import com.soloist.player.model.entity.gear.dto.GearItemView
import org.babyfish.jimmer.Page

interface GearItemService {

	fun create(input: GearItemInput, imageFileId: String): GearItem

	fun find(pageIndex: Int, pageSize: Int): Page<GearItemView>
}
