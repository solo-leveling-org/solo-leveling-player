package com.sleepkqq.sololeveling.player.service.service.player

import com.sleepkqq.sololeveling.player.model.entity.player.Level
import java.util.UUID

interface LevelService {
	fun get(id: UUID): Level
	fun find(id: UUID): Level?
	fun initializePlayerLevel(): Level
	fun initializeTopicLevel(): Level
}
