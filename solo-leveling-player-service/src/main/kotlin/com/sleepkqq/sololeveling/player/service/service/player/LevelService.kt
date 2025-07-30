package com.sleepkqq.sololeveling.player.service.service.player

import com.sleepkqq.sololeveling.player.model.entity.player.Level

interface LevelService {

	fun initializePlayerLevel(): Level
	fun initializeTopicLevel(): Level
}
