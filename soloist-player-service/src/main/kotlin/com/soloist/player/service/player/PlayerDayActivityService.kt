package com.soloist.player.service.player

import com.soloist.player.model.entity.player.PlayerDayActivity

interface PlayerDayActivityService {

	fun insertIfAbsent(activity: PlayerDayActivity): PlayerDayActivity
	fun getMonthlyActivity(playerId: Long, year: Int, month: Int): List<Int>
}
