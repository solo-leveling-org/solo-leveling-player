package com.sleepkqq.sololeveling.player.service.service.player

import com.sleepkqq.sololeveling.player.model.entity.player.enums.LevelType
import org.springframework.stereotype.Service

@Service
class CountExperienceService {

	private companion object {
		const val FIRST_LEVEL_PLAYER_EXPERIENCE = 100
		const val NEXT_LEVEL_PLAYER_EXPERIENCE = 10

		const val PLAYER_TASK_TOPIC_COEFFICIENT = 5
		const val FIRST_LEVEL_TOPIC_EXPERIENCE =
			FIRST_LEVEL_PLAYER_EXPERIENCE / PLAYER_TASK_TOPIC_COEFFICIENT
		const val NEXT_LEVEL_TOPIC_EXPERIENCE =
			NEXT_LEVEL_PLAYER_EXPERIENCE / PLAYER_TASK_TOPIC_COEFFICIENT
	}

	fun countExperienceToNextLevel(level: Int, levelType: LevelType): Int =
		when (levelType) {
			LevelType.PLAYER -> countExperienceToNextLevel(
				level,
				FIRST_LEVEL_PLAYER_EXPERIENCE,
				NEXT_LEVEL_PLAYER_EXPERIENCE
			)

			LevelType.TASK_TOPIC -> countExperienceToNextLevel(
				level,
				FIRST_LEVEL_TOPIC_EXPERIENCE,
				NEXT_LEVEL_TOPIC_EXPERIENCE
			)
		}

	private fun countExperienceToNextLevel(
		level: Int,
		firstLevelExperience: Int,
		nextLevelExperience: Int
	): Int = firstLevelExperience + (nextLevelExperience * (level - 1))
}
