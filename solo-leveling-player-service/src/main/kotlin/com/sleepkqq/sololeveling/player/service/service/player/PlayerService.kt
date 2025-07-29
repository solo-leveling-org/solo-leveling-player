package com.sleepkqq.sololeveling.player.service.service.player

import com.sleepkqq.sololeveling.player.model.entity.player.Player
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerView
import java.time.LocalDateTime

interface PlayerService {
	fun find(id: Long): PlayerView?
	fun get(id: Long): PlayerView
	fun insert(player: Player): Player
	fun update(player: Player, now: LocalDateTime = LocalDateTime.now()): Player
}
