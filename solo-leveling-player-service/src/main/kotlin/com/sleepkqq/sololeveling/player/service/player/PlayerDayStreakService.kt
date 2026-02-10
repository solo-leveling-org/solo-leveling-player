package com.sleepkqq.sololeveling.player.service.player

import com.sleepkqq.sololeveling.player.exception.ModelNotFoundException
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerDayStreak

interface PlayerDayStreakService {

	fun find(playerId: Long): PlayerDayStreak?
	fun get(playerId: Long): PlayerDayStreak = find(playerId)
		?: throw ModelNotFoundException(PlayerDayStreak::class, playerId)

	fun extend(dayStreak: PlayerDayStreak): PlayerDayStreak
	fun initialize(): PlayerDayStreak
	fun update(dayStreak: PlayerDayStreak): PlayerDayStreak
	fun processStreak(playerId: Long): PlayerDayStreak
}
