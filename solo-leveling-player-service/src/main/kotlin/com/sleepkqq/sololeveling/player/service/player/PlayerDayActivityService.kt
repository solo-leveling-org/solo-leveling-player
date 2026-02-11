package com.sleepkqq.sololeveling.player.service.player

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerDayActivity

interface PlayerDayActivityService {

	fun insertIfAbsent(activity: PlayerDayActivity): PlayerDayActivity
}
