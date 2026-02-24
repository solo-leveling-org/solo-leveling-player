package com.soloist.player.service.player

import com.soloist.player.exception.ModelNotFoundException
import com.soloist.player.model.entity.player.PlayerDayStreak
import java.time.LocalDate

interface PlayerDayStreakService {

	fun find(playerId: Long): PlayerDayStreak?
	fun get(playerId: Long): PlayerDayStreak = find(playerId)
		?: throw ModelNotFoundException(PlayerDayStreak::class, playerId)

	fun extend(dayStreak: PlayerDayStreak, today: LocalDate = LocalDate.now()): PlayerDayStreak
	fun initialize(): PlayerDayStreak
	fun update(dayStreak: PlayerDayStreak): PlayerDayStreak
	fun processStreak(playerId: Long, today: LocalDate = LocalDate.now()): PlayerDayStreak
	fun resetExpiredStreaks(): Long
}
