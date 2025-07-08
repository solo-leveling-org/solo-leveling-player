package com.sleepkqq.sololeveling.player.service.service.player

import com.sleepkqq.sololeveling.player.model.entity.player.Player
import com.sleepkqq.sololeveling.player.service.exception.ModelNotFoundException
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

@Service
@Profile("test")
class PlayerServiceTestImpl : PlayerService {

	private val players = ConcurrentHashMap<Long, Player>()

	override fun get(id: Long): Player = find(id) ?: throw ModelNotFoundException(Player::class, id)

	override fun insert(player: Player): Player {
		if (players.containsKey(player.id)) {
			throw IllegalStateException("Player with id ${player.id} already exists")
		}
		players[player.id] = player
		return player
	}

	override fun update(player: Player, now: LocalDateTime): Player {
		if (!players.containsKey(player.id)) {
			throw IllegalStateException("Player with id ${player.id} not found")
		}
		val updated = Player(player) { updatedAt = now }
		players[player.id] = updated
		return updated
	}

	override fun update(player: Player): Player = update(player, LocalDateTime.now())

	override fun find(id: Long): Player? = players[id]

	fun clear() = players.clear()
} 