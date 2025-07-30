package com.sleepkqq.sololeveling.player.service.service.player

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTaskTopic
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic
import java.time.LocalDateTime

interface PlayerTaskTopicService {

	fun initialize(playerId: Long, taskTopic: TaskTopic): PlayerTaskTopic
	fun insert(topic: PlayerTaskTopic): PlayerTaskTopic
	fun saveAll(topics: Collection<PlayerTaskTopic>): List<PlayerTaskTopic>
	fun update(
		playerTaskTopic: PlayerTaskTopic,
		now: LocalDateTime = LocalDateTime.now()
	): PlayerTaskTopic

	fun getActiveTopics(playerId: Long): List<PlayerTaskTopic>
	fun getTopics(playerId: Long): List<PlayerTaskTopic>
}
