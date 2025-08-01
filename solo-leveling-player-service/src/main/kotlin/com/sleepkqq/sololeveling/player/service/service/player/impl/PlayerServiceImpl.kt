package com.sleepkqq.sololeveling.player.service.service.player.impl

import com.sleepkqq.sololeveling.player.model.entity.player.Player
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerFetcherDsl
import com.sleepkqq.sololeveling.player.model.entity.player.by
import com.sleepkqq.sololeveling.player.model.repository.player.PlayerRepository
import com.sleepkqq.sololeveling.player.service.exception.ModelNotFoundException
import com.sleepkqq.sololeveling.player.service.service.player.PlayerService
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.babyfish.jimmer.sql.kt.fetcher.newFetcher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Suppress("unused")
@Service
class PlayerServiceImpl(
	private val playerRepository: PlayerRepository
) : PlayerService {

	@Transactional(readOnly = true)
	override fun find(id: Long, block: PlayerFetcherDsl.() -> Unit): Player? =
		playerRepository.findNullable(id, newFetcher(Player::class).by(block))

	@Transactional(readOnly = true)
	override fun get(id: Long, block: PlayerFetcherDsl.() -> Unit): Player = find(id, block)
		?: throw ModelNotFoundException(Player::class, id)

	@Transactional
	override fun insert(player: Player): Player {
		return playerRepository.save(player, SaveMode.INSERT_ONLY)
	}

	@Transactional
	override fun update(player: Player, now: LocalDateTime): Player =
		playerRepository.save(
			Player(player) { updatedAt = now },
			SaveMode.UPDATE_ONLY
		)
}
