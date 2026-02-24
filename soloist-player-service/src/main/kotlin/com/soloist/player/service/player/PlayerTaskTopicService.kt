package com.soloist.player.service.player

import com.soloist.player.model.entity.player.PlayerTaskTopic
import com.soloist.player.model.entity.player.dto.PlayerTaskTopicView
import com.soloist.player.model.entity.task.enums.TaskTopic

interface PlayerTaskTopicService {

	fun initialize(taskTopic: TaskTopic): PlayerTaskTopic
	fun insert(topic: PlayerTaskTopic): PlayerTaskTopic
	fun updateAll(topics: Collection<PlayerTaskTopic>)
	fun update(playerTaskTopic: PlayerTaskTopic): PlayerTaskTopic
	fun getByPlayerId(playerId: Long): List<PlayerTaskTopicView>
}
