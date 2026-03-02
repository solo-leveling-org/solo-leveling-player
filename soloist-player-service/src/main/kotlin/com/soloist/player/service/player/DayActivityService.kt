package com.soloist.player.service.player

import com.soloist.player.model.entity.player.DayActivity

interface DayActivityService {

	fun insertIfAbsent(activity: DayActivity): DayActivity
	fun getMonthlyActivity(playerId: Long, year: Int, month: Int): List<Int>
}
