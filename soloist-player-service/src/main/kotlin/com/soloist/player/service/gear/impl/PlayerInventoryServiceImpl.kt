package com.soloist.player.service.gear.impl

import com.soloist.player.model.entity.player.dto.PlayerGearItemView
import com.soloist.player.model.repository.player.PlayerGearItemRepository
import com.soloist.player.service.gear.PlayerInventoryService
import org.babyfish.jimmer.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PlayerInventoryServiceImpl(
	private val playerGearItemRepository: PlayerGearItemRepository
) : PlayerInventoryService {

	@Transactional(readOnly = true)
	override fun getInventory(playerId: Long, pageIndex: Int, pageSize: Int): Page<PlayerGearItemView> =
		playerGearItemRepository.findByPlayerIdView(playerId, pageIndex, pageSize, PlayerGearItemView::class.java)
}
