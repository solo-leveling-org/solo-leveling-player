package com.sleepkqq.sololeveling.player.service.service.player

import org.springframework.stereotype.Service

@Service
class CountExperienceService {

	companion object {
		private const val FIRST_LEVEL_PLAYER_EXPERIENCE = 100
		private const val NEXT_LEVEL_PLAYER_EXPERIENCE = 10

		private const val PLAYER_TASK_TOPIC_COEFFICIENT = 5
		private const val FIRST_LEVEL_TOPIC_EXPERIENCE =
			FIRST_LEVEL_PLAYER_EXPERIENCE / PLAYER_TASK_TOPIC_COEFFICIENT
		private const val NEXT_LEVEL_TOPIC_EXPERIENCE =
			NEXT_LEVEL_PLAYER_EXPERIENCE / PLAYER_TASK_TOPIC_COEFFICIENT
	}

	fun countPlayerExperienceToNextLevel(level: Int): Int =
		countExperienceToNextLevel(
			level,
			FIRST_LEVEL_PLAYER_EXPERIENCE,
			NEXT_LEVEL_PLAYER_EXPERIENCE
		)

	fun countTopicExperienceToNextLevel(level: Int): Int =
		countExperienceToNextLevel(
			level,
			FIRST_LEVEL_TOPIC_EXPERIENCE,
			NEXT_LEVEL_TOPIC_EXPERIENCE
		)

	private fun countExperienceToNextLevel(
		level: Int,
		firstLevelExperience: Int,
		nextLevelExperience: Int
	): Int = firstLevelExperience + (nextLevelExperience * (level - 1))
}
