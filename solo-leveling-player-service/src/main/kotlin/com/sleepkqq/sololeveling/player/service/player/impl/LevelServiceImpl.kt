package com.sleepkqq.sololeveling.player.service.player.impl

import com.sleepkqq.sololeveling.player.model.entity.Immutables
import com.sleepkqq.sololeveling.player.model.entity.player.Level
import com.sleepkqq.sololeveling.player.model.entity.player.Player
import com.sleepkqq.sololeveling.player.model.entity.player.TaskTopicItem
import com.sleepkqq.sololeveling.player.model.entity.player.enums.Assessment
import com.sleepkqq.sololeveling.player.model.entity.player.enums.LevelType
import com.sleepkqq.sololeveling.player.service.player.CountExperienceService
import com.sleepkqq.sololeveling.player.service.player.LevelService
import com.sleepkqq.sololeveling.player.service.task.DefineTaskTopicService.Companion.MAX_TASK_TOPICS_COUNT
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class LevelServiceImpl(
	private val countExperienceService: CountExperienceService
) : LevelService {

	private companion object {
		const val INITIAL_LEVEL = 1
		const val INITIAL_EXPERIENCE = 0
	}

	override fun initializeLevel(levelType: LevelType): Level =
		Immutables.createLevel {
			it.setId(UUID.randomUUID())
			it.setLevel(INITIAL_LEVEL)
			it.setTotalExperience(INITIAL_EXPERIENCE)
			it.setCurrentExperience(INITIAL_EXPERIENCE)
			it.setExperienceToNextLevel(
				countExperienceService.countExperienceToNextLevel(
					INITIAL_LEVEL,
					levelType
				)
			)
			it.setAssessment(Assessment.E)
		}

	override fun gainExperience(
		player: Player,
		taskTopics: Collection<TaskTopicItem>,
		experience: Int
	): Player {

		require(taskTopics.isNotEmpty() && taskTopics.size <= MAX_TASK_TOPICS_COUNT) {
			"taskTopics size=${taskTopics.size} must be greater than 0 and less or equals $MAX_TASK_TOPICS_COUNT"
		}

		val playerTaskTopicsMap = player.taskTopics()
			.associateBy { it.taskTopic() }
			.toMutableMap()

		taskTopics.forEach {
			val topic = it.topic()
			val playerTaskTopic = playerTaskTopicsMap[topic]!!
			val processedTaskTopicLevel = processExperienceGain(
				playerTaskTopic.level()!!,
				LevelType.TASK_TOPIC,
				experience / taskTopics.size
			)
			playerTaskTopicsMap[topic] = Immutables.createPlayerTaskTopic(playerTaskTopic) { p ->
				p.setLevel(processedTaskTopicLevel)
			}
		}

		return Immutables.createPlayer(player) {
			it.setLevel(processExperienceGain(player.level()!!, LevelType.PLAYER, experience))
			it.setTaskTopics(playerTaskTopicsMap.values.toList())
		}
	}

	private fun processExperienceGain(level: Level, levelType: LevelType, experience: Int): Level {
		var updatedLevel = level.level()
		var updatedCurrentExperience = level.currentExperience() + experience
		var updatedExperienceToNextLevel = level.experienceToNextLevel()

		while (updatedCurrentExperience >= updatedExperienceToNextLevel) {
			updatedLevel += 1
			updatedCurrentExperience -= updatedExperienceToNextLevel
			updatedExperienceToNextLevel = countExperienceService.countExperienceToNextLevel(
				updatedLevel,
				levelType
			)
		}

		return Immutables.createLevel(level) {
			it.setLevel(updatedLevel)
			it.setCurrentExperience(updatedCurrentExperience)
			it.setTotalExperience(level.totalExperience() + experience)
			it.setExperienceToNextLevel(updatedExperienceToNextLevel)
		}
	}
}

