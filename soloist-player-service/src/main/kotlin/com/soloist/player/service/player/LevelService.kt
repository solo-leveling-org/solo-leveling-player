package com.soloist.player.service.player

import com.soloist.player.model.entity.player.Level
import com.soloist.player.model.entity.player.Player
import com.soloist.player.model.entity.player.enums.LevelType
import com.soloist.player.model.entity.task.enums.TaskTopic

interface LevelService {

	fun initialize(levelType: LevelType): Level
	fun gainExperience(player: Player, taskTopics: Collection<TaskTopic>, experience: Int): Player
}
