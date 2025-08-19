package com.sleepkqq.sololeveling.player.service.service.player

import com.sleepkqq.sololeveling.player.model.entity.player.Player
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerFetcherDsl
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerView
import com.sleepkqq.sololeveling.player.service.exception.ModelNotFoundException
import java.time.LocalDateTime

interface PlayerService {

	fun find(id: Long, block: PlayerFetcherDsl.() -> Unit = { allScalarFields() }): Player?
	fun get(id: Long, block: PlayerFetcherDsl.() -> Unit = { allScalarFields() }): Player =
		find(id, block) ?: throw ModelNotFoundException(Player::class, id)

	fun findView(id: Long): PlayerView?
	fun getView(id: Long): PlayerView = findView(id)
		?: throw ModelNotFoundException(Player::class, id)

	fun insert(player: Player): Player
	fun update(player: Player, now: LocalDateTime = LocalDateTime.now()): Player
}
