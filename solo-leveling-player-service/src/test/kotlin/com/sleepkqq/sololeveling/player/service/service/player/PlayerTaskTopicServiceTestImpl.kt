package com.sleepkqq.sololeveling.player.service.service.player

import com.sleepkqq.sololeveling.player.model.entity.player.Level
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTaskTopic
import com.sleepkqq.sololeveling.player.model.entity.player.enums.Assessment
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Suppress("unused")
@Service
@Profile("test")
class PlayerTaskTopicServiceTestImpl : PlayerTaskTopicService {

	private val playerTaskTopics = ConcurrentHashMap<UUID, PlayerTaskTopic>()

	override fun initialize(linkedPlayerId: Long, linkedTaskTopic: TaskTopic): PlayerTaskTopic =
		PlayerTaskTopic {
			id = UUID.randomUUID()
			taskTopic = linkedTaskTopic
			playerId = linkedPlayerId
			level = Level {
				id = UUID.randomUUID()
				level = 1
				totalExperience = 0
				currentExperience = 0
				experienceToNextLevel = 100
				assessment = Assessment.E
			}
		}

	override fun insert(topic: PlayerTaskTopic): PlayerTaskTopic {
		if (playerTaskTopics.containsKey(topic.id)) {
			throw IllegalStateException("PlayerTaskTopic with id ${topic.id} already exists")
		}
		playerTaskTopics[topic.id] = topic
		return topic
	}

	override fun insertAll(topics: Collection<PlayerTaskTopic>) = topics.map(this::insert)

	override fun update(playerTaskTopic: PlayerTaskTopic, now: LocalDateTime): PlayerTaskTopic {
		if (!playerTaskTopics.containsKey(playerTaskTopic.id)) {
			throw IllegalStateException("PlayerTaskTopic with id ${playerTaskTopic.id} not found")
		}
		val updated = PlayerTaskTopic(playerTaskTopic) { updatedAt = now }
		playerTaskTopics[playerTaskTopic.id] = updated
		return updated
	}

	fun clear() = playerTaskTopics.clear()
}
