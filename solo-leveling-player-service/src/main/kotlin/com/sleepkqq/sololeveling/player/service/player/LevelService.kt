package com.sleepkqq.sololeveling.player.service.player

import com.sleepkqq.sololeveling.player.model.entity.player.Level
import com.sleepkqq.sololeveling.player.model.entity.player.Player
import com.sleepkqq.sololeveling.player.model.entity.player.enums.LevelType
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic

interface LevelService {

	fun initializeLevel(levelType: LevelType): Level
	fun gainExperience(player: Player, taskTopics: Set<TaskTopic>, experience: Int): Player
}
