package com.sleepkqq.sololeveling.player.service.service.player.impl

import com.sleepkqq.sololeveling.player.model.entity.player.Player
import com.sleepkqq.sololeveling.player.model.repository.player.PlayerRepository
import com.sleepkqq.sololeveling.player.service.exception.ModelNotFoundException
import com.sleepkqq.sololeveling.player.service.service.player.PlayerService
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Suppress("unused")
@Service
@Profile("!test")
class PlayerServiceImpl(
	private val playerRepository: PlayerRepository
) : PlayerService {

	@Transactional(readOnly = true)
	override fun get(id: Long): Player = find(id)
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

	@Transactional(readOnly = true)
	override fun find(id: Long): Player? = playerRepository.findNullable(id)
}
