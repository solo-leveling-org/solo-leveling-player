package com.sleepkqq.sololeveling.player.service.service.player

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTaskTopic
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic
import java.time.LocalDateTime

interface PlayerTaskTopicService {
	fun initialize(linkedPlayerId: Long, linkedTaskTopic: TaskTopic): PlayerTaskTopic
	fun insert(topic: PlayerTaskTopic): PlayerTaskTopic
	fun insertAll(topics: Collection<PlayerTaskTopic>): List<PlayerTaskTopic>
	fun update(
		playerTaskTopic: PlayerTaskTopic,
		now: LocalDateTime = LocalDateTime.now()
	): PlayerTaskTopic
}
