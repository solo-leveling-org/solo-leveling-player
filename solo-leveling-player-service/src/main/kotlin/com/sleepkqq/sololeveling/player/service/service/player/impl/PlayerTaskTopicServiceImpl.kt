package com.sleepkqq.sololeveling.player.service.service.player.impl

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTaskTopic
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerTaskTopicView
import com.sleepkqq.sololeveling.player.model.entity.player.enums.LevelType
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic
import com.sleepkqq.sololeveling.player.model.repository.player.PlayerTaskTopicRepository
import com.sleepkqq.sololeveling.player.service.service.player.LevelService
import com.sleepkqq.sololeveling.player.service.service.player.PlayerTaskTopicService
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Suppress("unused")
@Service
class PlayerTaskTopicServiceImpl(
	private val playerTaskTopicRepository: PlayerTaskTopicRepository,
	private val levelService: LevelService
) : PlayerTaskTopicService {

	override fun initialize(playerId: Long, taskTopic: TaskTopic): PlayerTaskTopic =
		PlayerTaskTopic {
			id = UUID.randomUUID()
			this.taskTopic = taskTopic
			this.playerId = playerId
			level = levelService.initializeLevel(LevelType.TASK_TOPIC)
			isActive = true
		}

	@Transactional
	override fun insert(topic: PlayerTaskTopic): PlayerTaskTopic =
		playerTaskTopicRepository.save(topic, SaveMode.INSERT_ONLY)

	@Transactional
	override fun saveAll(topics: Collection<PlayerTaskTopic>) =
		playerTaskTopicRepository.upsertAll(topics)

	@Transactional
	override fun update(playerTaskTopic: PlayerTaskTopic, now: LocalDateTime): PlayerTaskTopic =
		playerTaskTopicRepository.save(
			PlayerTaskTopic(playerTaskTopic) { updatedAt = now },
			SaveMode.UPDATE_ONLY
		)

	@Transactional(readOnly = true)
	override fun getActiveTopics(playerId: Long): List<PlayerTaskTopicView> =
		playerTaskTopicRepository.findByPlayerIdAndIsActiveTrue(playerId)

	@Transactional(readOnly = true)
	override fun getTopics(playerId: Long): List<PlayerTaskTopic> =
		playerTaskTopicRepository.findByPlayerId(playerId)
}
