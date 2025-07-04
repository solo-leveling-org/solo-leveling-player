package com.sleepkqq.sololeveling.player.service.service.player

import com.sleepkqq.sololeveling.player.model.entity.player.Player
import com.sleepkqq.sololeveling.player.model.repository.player.PlayerRepository
import com.sleepkqq.sololeveling.player.service.exception.ModelNotFoundException
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class PlayerService(
	private val playerRepository: PlayerRepository
) {

	@Transactional(readOnly = true)
	fun get(id: Long): Player = find(id)
		?: throw ModelNotFoundException(Player::class, id)

	@Transactional
	fun insert(player: Player): Player {
		return playerRepository.save(player, SaveMode.INSERT_ONLY)
	}

	@Transactional
	fun update(player: Player, now: LocalDateTime): Player =
		playerRepository.save(
			Player(player) { updatedAt = now },
			SaveMode.UPDATE_ONLY
		)

	@Transactional
	fun update(player: Player): Player = update(player, LocalDateTime.now())

	@Transactional(readOnly = true)
	fun find(id: Long): Player? = playerRepository.findNullable(id)
}