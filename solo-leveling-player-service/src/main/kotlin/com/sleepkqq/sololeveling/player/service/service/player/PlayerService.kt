package com.sleepkqq.sololeveling.player.service.service.player

import com.sleepkqq.sololeveling.player.model.entity.player.Player
import java.time.LocalDateTime

interface PlayerService {
	fun get(id: Long): Player
	fun insert(player: Player): Player
	fun update(player: Player, now: LocalDateTime): Player
	fun update(player: Player): Player
	fun find(id: Long): Player?
} 