package com.sleepkqq.sololeveling.player.service.service.player

import com.sleepkqq.sololeveling.player.model.entity.player.Player
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerFetcherDsl
import java.time.LocalDateTime

interface PlayerService {

	fun find(id: Long, block: PlayerFetcherDsl.() -> Unit = {}): Player?
	fun get(id: Long, block: PlayerFetcherDsl.() -> Unit = {}): Player
	fun insert(player: Player): Player
	fun update(player: Player, now: LocalDateTime = LocalDateTime.now()): Player
}
