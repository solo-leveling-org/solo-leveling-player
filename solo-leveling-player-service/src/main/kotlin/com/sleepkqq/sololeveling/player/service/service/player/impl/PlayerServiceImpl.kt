package com.sleepkqq.sololeveling.player.service.service.player.impl

import com.sleepkqq.sololeveling.player.model.entity.Immutables
import com.sleepkqq.sololeveling.player.model.entity.player.Player
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerFetcher
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerView
import com.sleepkqq.sololeveling.player.model.repository.player.PlayerRepository
import com.sleepkqq.sololeveling.player.service.service.player.PlayerService
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Suppress("unused")
@Service
class PlayerServiceImpl(
	private val playerRepository: PlayerRepository
) : PlayerService {

	@Transactional(readOnly = true)
	override fun find(id: Long, fetcher: PlayerFetcher): Player? =
		playerRepository.findNullable(id, fetcher)

	@Transactional(readOnly = true)
	override fun findView(id: Long): PlayerView? =
		playerRepository.viewer(PlayerView::class.java)
			.findNullable(id)

	@Transactional
	override fun insert(player: Player): Player =
		playerRepository.save(player, SaveMode.INSERT_ONLY)

	@Transactional
	override fun update(player: Player, now: LocalDateTime): Player =
		playerRepository.save(
			Immutables.createPlayer(player) {
				it.setUpdatedAt(now)
			},
			SaveMode.UPDATE_ONLY
		)
}
