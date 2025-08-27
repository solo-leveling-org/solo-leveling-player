package com.sleepkqq.sololeveling.player.service.service.player.impl

import com.sleepkqq.sololeveling.player.model.entity.Immutables
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
		Immutables.createPlayerTaskTopic {
			it.setId(UUID.randomUUID())
			it.setTaskTopic(taskTopic)
			it.setPlayerId(playerId)
			it.setLevel(levelService.initializeLevel(LevelType.TASK_TOPIC))
			it.setActive(false)
		}

	@Transactional
	override fun insert(topic: PlayerTaskTopic): PlayerTaskTopic =
		playerTaskTopicRepository.save(topic, SaveMode.INSERT_ONLY)

	@Transactional
	override fun updateAll(topics: Collection<PlayerTaskTopic>) {
		playerTaskTopicRepository.saveEntities(topics, SaveMode.UPDATE_ONLY)
	}

	@Transactional
	override fun update(playerTaskTopic: PlayerTaskTopic, now: LocalDateTime): PlayerTaskTopic =
		playerTaskTopicRepository.save(
			Immutables.createPlayerTaskTopic(playerTaskTopic) {
				it.setUpdatedAt(now)
			},
			SaveMode.UPDATE_ONLY
		)

	@Transactional(readOnly = true)
	override fun getByPlayerId(playerId: Long): List<PlayerTaskTopicView> =
		playerTaskTopicRepository.findViewByPlayerId(playerId, PlayerTaskTopicView::class.java)
}
