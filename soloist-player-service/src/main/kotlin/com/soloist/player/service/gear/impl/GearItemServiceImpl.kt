package com.soloist.player.service.gear.impl

import com.soloist.player.model.entity.gear.GearItem
import com.soloist.player.model.entity.gear.dto.GearItemInput
import com.soloist.player.model.entity.gear.dto.GearItemView
import com.soloist.player.model.repository.gear.GearItemRepository
import com.soloist.player.service.gear.GearItemService
import org.babyfish.jimmer.Page
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GearItemServiceImpl(
	private val gearItemRepository: GearItemRepository
) : GearItemService {

	private val log = LoggerFactory.getLogger(javaClass)

	@Transactional
	override fun create(input: GearItemInput, imageFileId: String): GearItem {
		input.imageFileId = imageFileId
		val gearItem = gearItemRepository.save(input.toEntity(), SaveMode.INSERT_ONLY)

		log.info("Created gear item id={}, type={}, rarity={}", gearItem.id(), input.type, input.rarity)

		return gearItem
	}

	@Transactional(readOnly = true)
	override fun find(pageIndex: Int, pageSize: Int): Page<GearItemView> =
		gearItemRepository.find(pageIndex, pageSize, GearItemView::class.java)
}
