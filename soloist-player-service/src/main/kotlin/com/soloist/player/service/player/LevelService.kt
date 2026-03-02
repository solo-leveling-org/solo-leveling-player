package com.soloist.player.service.player

import com.soloist.player.exception.ModelNotFoundException
import com.soloist.player.model.entity.player.Level
import com.soloist.player.model.entity.player.Player
import com.soloist.player.model.entity.player.enums.LevelType
import com.soloist.player.model.entity.task.enums.TaskTopic
import org.babyfish.jimmer.View
import kotlin.reflect.KClass

interface LevelService {

	fun <V : View<Level>> findView(playerId: Long, viewType: KClass<V>): V?
	fun <V : View<Level>> getView(playerId: Long, viewType: KClass<V>): V =
		findView(playerId, viewType) ?: throw ModelNotFoundException(Level::class, playerId)

	fun initialize(levelType: LevelType): Level
	fun gainExperience(player: Player, taskTopics: Collection<TaskTopic>, experience: Int): Player
}
