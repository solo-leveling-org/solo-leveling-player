package com.soloist.player.service.task

import com.soloist.player.model.entity.task.PlayerTaskTopic
import com.soloist.player.model.entity.task.enums.TaskTopic
import org.babyfish.jimmer.View
import kotlin.reflect.KClass

interface PlayerTaskTopicService {

	fun initialize(taskTopic: TaskTopic): PlayerTaskTopic
	fun insert(topic: PlayerTaskTopic): PlayerTaskTopic
	fun updateAll(topics: Collection<PlayerTaskTopic>)
	fun update(playerTaskTopic: PlayerTaskTopic): PlayerTaskTopic
	fun <V : View<PlayerTaskTopic>> findView(playerId: Long, viewType: KClass<V>): List<V>
}
