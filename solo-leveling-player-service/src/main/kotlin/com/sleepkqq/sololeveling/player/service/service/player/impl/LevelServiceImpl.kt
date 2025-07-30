package com.sleepkqq.sololeveling.player.service.service.player.impl

import com.sleepkqq.sololeveling.player.model.entity.player.Level
import com.sleepkqq.sololeveling.player.model.entity.player.enums.Assessment
import com.sleepkqq.sololeveling.player.service.service.player.CountExperienceService
import com.sleepkqq.sololeveling.player.service.service.player.LevelService
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

		val BASE_LEVEL = Level {
			level = INITIAL_LEVEL
			totalExperience = INITIAL_EXPERIENCE
			currentExperience = INITIAL_EXPERIENCE
			assessment = Assessment.E
		}
	}

	override fun initializePlayerLevel(): Level = Level(BASE_LEVEL) {
		id = UUID.randomUUID()
		experienceToNextLevel =
			countExperienceService.countPlayerExperienceToNextLevel(INITIAL_LEVEL)
	}

	override fun initializeTopicLevel(): Level = Level(BASE_LEVEL) {
		id = UUID.randomUUID()
		experienceToNextLevel =
			countExperienceService.countTopicExperienceToNextLevel(INITIAL_LEVEL)
	}
}
