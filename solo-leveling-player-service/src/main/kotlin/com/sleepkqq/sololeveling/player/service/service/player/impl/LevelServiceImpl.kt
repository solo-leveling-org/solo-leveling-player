package com.sleepkqq.sololeveling.player.service.service.player.impl

import com.sleepkqq.sololeveling.player.model.entity.player.Level
import com.sleepkqq.sololeveling.player.model.entity.player.Player
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTaskTopic
import com.sleepkqq.sololeveling.player.model.entity.player.enums.Assessment
import com.sleepkqq.sololeveling.player.model.entity.player.enums.LevelType
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic
import com.sleepkqq.sololeveling.player.service.service.player.CountExperienceService
import com.sleepkqq.sololeveling.player.service.service.player.LevelService
import com.sleepkqq.sololeveling.player.service.service.task.DefineTaskTopicService.Companion.MAX_TASK_TOPICS_COUNT
import org.springframework.stereotype.Service
import java.util.UUID

@Suppress("unused")
@Service
class LevelServiceImpl(
	private val countExperienceService: CountExperienceService
) : LevelService {

	private companion object {
		const val INITIAL_LEVEL = 1
		const val INITIAL_EXPERIENCE = 0
	}

	override fun initializeLevel(levelType: LevelType): Level = Level {
		id = UUID.randomUUID()
		level = INITIAL_LEVEL
		totalExperience = INITIAL_EXPERIENCE
		currentExperience = INITIAL_EXPERIENCE
		experienceToNextLevel = countExperienceService.countExperienceToNextLevel(
			INITIAL_LEVEL,
			levelType
		)
		assessment = Assessment.E
	}

	override fun gainExperience(
		player: Player,
		taskTopics: Set<TaskTopic>,
		experience: Int
	): Player {

		require(taskTopics.isNotEmpty() && taskTopics.size <= MAX_TASK_TOPICS_COUNT) {
			"taskTopics size=${taskTopics.size} must be greater than 0 and less or equals $MAX_TASK_TOPICS_COUNT"
		}

		val playerTaskTopicsMap = player.taskTopics
			.associateBy { it.taskTopic }
			.toMutableMap()

		taskTopics.forEach {
			val playerTaskTopic = playerTaskTopicsMap[it]!!
			val processedTaskTopicLevel = processExperienceGain(
				playerTaskTopic.level!!,
				LevelType.TASK_TOPIC,
				experience / taskTopics.size
			)
			playerTaskTopicsMap[it] = PlayerTaskTopic(playerTaskTopic) {
				level = processedTaskTopicLevel
			}
		}

		return Player(player) {
			level = processExperienceGain(player.level!!, LevelType.PLAYER, experience)
			this.taskTopics = playerTaskTopicsMap.values.toList()
		}
	}

	private fun processExperienceGain(level: Level, levelType: LevelType, experience: Int): Level {
		var updatedLevel = level.level
		var updatedCurrentExperience = level.currentExperience + experience
		var updatedExperienceToNextLevel = level.experienceToNextLevel

		while (updatedCurrentExperience >= updatedExperienceToNextLevel) {
			updatedLevel += 1
			updatedCurrentExperience -= updatedExperienceToNextLevel
			updatedExperienceToNextLevel = countExperienceService.countExperienceToNextLevel(
				updatedLevel,
				levelType
			)
		}

		return Level(level) {
			this.level = updatedLevel
			currentExperience = updatedCurrentExperience
			totalExperience = level.totalExperience + experience
			experienceToNextLevel = updatedExperienceToNextLevel
		}
	}
}

