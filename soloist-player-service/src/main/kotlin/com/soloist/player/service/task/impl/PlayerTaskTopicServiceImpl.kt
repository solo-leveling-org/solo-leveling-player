package com.soloist.player.service.task.impl

import com.soloist.player.model.entity.Immutables
import com.soloist.player.model.entity.task.PlayerTaskTopic
import com.soloist.player.model.entity.player.enums.LevelType
import com.soloist.player.model.entity.task.enums.TaskTopic
import com.soloist.player.model.repository.task.PlayerTaskTopicRepository
import com.soloist.player.service.player.LevelService
import com.soloist.player.service.task.PlayerTaskTopicService
import org.babyfish.jimmer.View
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import kotlin.reflect.KClass

@Service
class PlayerTaskTopicServiceImpl(
	private val playerTaskTopicRepository: PlayerTaskTopicRepository,
	private val levelService: LevelService
) : PlayerTaskTopicService {

	override fun initialize(taskTopic: TaskTopic): PlayerTaskTopic =
		Immutables.createPlayerTaskTopic {
			it.setId(UUID.randomUUID())
			it.setTaskTopic(taskTopic)
			it.setLevel(levelService.initialize(LevelType.TASK_TOPIC))
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
	override fun update(playerTaskTopic: PlayerTaskTopic): PlayerTaskTopic =
		playerTaskTopicRepository.save(playerTaskTopic, SaveMode.UPDATE_ONLY)

	@Transactional(readOnly = true)
	override fun <V : View<PlayerTaskTopic>> findView(playerId: Long, viewType: KClass<V>): List<V> =
		playerTaskTopicRepository.findViewByPlayerId(playerId, viewType.java)
}
