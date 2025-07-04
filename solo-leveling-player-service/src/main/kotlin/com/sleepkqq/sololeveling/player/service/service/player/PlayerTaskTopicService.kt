package com.sleepkqq.sololeveling.player.service.service.player

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTaskTopic
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic
import com.sleepkqq.sololeveling.player.model.repository.player.PlayerTaskTopicRepository
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class PlayerTaskTopicService(
	private val playerTaskTopicRepository: PlayerTaskTopicRepository,
	private val levelService: LevelService
) {

	fun initialize(linkedPlayerId: Long, linkedTaskTopic: TaskTopic): PlayerTaskTopic =
		PlayerTaskTopic {
			id = UUID.randomUUID()
			taskTopic = linkedTaskTopic
			playerId = linkedPlayerId
			level = levelService.initializeTopicLevel()
		}

	@Transactional
	fun insert(topic: PlayerTaskTopic): PlayerTaskTopic =
		playerTaskTopicRepository.save(topic, SaveMode.INSERT_ONLY)

	@Transactional
	fun update(playerTaskTopic: PlayerTaskTopic, now: LocalDateTime): PlayerTaskTopic {
		return playerTaskTopicRepository.save(
			PlayerTaskTopic(playerTaskTopic) { updatedAt = now },
			SaveMode.UPDATE_ONLY
		)
	}

	@Transactional
	fun update(topic: PlayerTaskTopic): PlayerTaskTopic =
		update(topic, LocalDateTime.now())
}