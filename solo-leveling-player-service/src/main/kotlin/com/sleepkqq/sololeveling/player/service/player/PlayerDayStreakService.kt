package com.sleepkqq.sololeveling.player.service.player

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerDayStreak

interface PlayerDayStreakService {

	fun extend(dayStreak: PlayerDayStreak): PlayerDayStreak
	fun initialize(): PlayerDayStreak
}
