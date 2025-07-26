package com.sleepkqq.sololeveling.player.service.service.player

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTaskTopic
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic
import com.sleepkqq.sololeveling.player.model.repository.player.PlayerTaskTopicRepository
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Suppress("unused")
@Service
@Profile("!test")
class PlayerTaskTopicServiceImpl(
	private val playerTaskTopicRepository: PlayerTaskTopicRepository,
	private val levelService: LevelService
) : PlayerTaskTopicService {

	override fun initialize(linkedPlayerId: Long, linkedTaskTopic: TaskTopic): PlayerTaskTopic =
		PlayerTaskTopic {
			id = UUID.randomUUID()
			taskTopic = linkedTaskTopic
			playerId = linkedPlayerId
			level = levelService.initializeTopicLevel()
		}

	@Transactional
	override fun insert(topic: PlayerTaskTopic): PlayerTaskTopic =
		playerTaskTopicRepository.save(topic, SaveMode.INSERT_ONLY)

	@Transactional
	override fun insertAll(topics: Collection<PlayerTaskTopic>) =
		playerTaskTopicRepository.saveAll(topics)

	@Transactional
	override fun update(playerTaskTopic: PlayerTaskTopic, now: LocalDateTime): PlayerTaskTopic {
		return playerTaskTopicRepository.save(
			PlayerTaskTopic(playerTaskTopic) { updatedAt = now },
			SaveMode.UPDATE_ONLY
		)
	}
}
