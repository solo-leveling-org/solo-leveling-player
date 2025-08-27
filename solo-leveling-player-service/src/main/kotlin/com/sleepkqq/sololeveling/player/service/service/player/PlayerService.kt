package com.sleepkqq.sololeveling.player.service.service.player

import com.sleepkqq.sololeveling.player.model.entity.Fetchers
import com.sleepkqq.sololeveling.player.model.entity.player.Player
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerFetcher
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerView
import com.sleepkqq.sololeveling.player.service.exception.ModelNotFoundException
import java.time.LocalDateTime

interface PlayerService {

	fun find(id: Long, fetcher: PlayerFetcher = Fetchers.PLAYER_FETCHER.allScalarFields()): Player?
	fun get(id: Long, fetcher: PlayerFetcher = Fetchers.PLAYER_FETCHER.allScalarFields()): Player =
		find(id, fetcher) ?: throw ModelNotFoundException(Player::class, id)

	fun findView(id: Long): PlayerView?
	fun getView(id: Long): PlayerView = findView(id)
		?: throw ModelNotFoundException(Player::class, id)

	fun insert(player: Player): Player
	fun update(player: Player, now: LocalDateTime = LocalDateTime.now()): Player
}
