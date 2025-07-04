package com.sleepkqq.sololeveling.player.service.service.player

import com.sleepkqq.sololeveling.player.model.entity.player.Level
import com.sleepkqq.sololeveling.player.model.entity.player.enums.Assessment
import com.sleepkqq.sololeveling.player.model.repository.player.LevelRepository
import com.sleepkqq.sololeveling.player.service.exception.ModelNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class LevelService(
	private val levelRepository: LevelRepository,
	private val countExperienceService: CountExperienceService
) {

	companion object {
		private const val BASE_FIRST_LEVEL = 1
		private const val BASE_BEGIN_EXPERIENCE = 0

		private val BASE_LEVEL = Level {
			id = UUID.randomUUID()
			level = BASE_FIRST_LEVEL
			totalExperience = BASE_BEGIN_EXPERIENCE
			currentExperience = BASE_BEGIN_EXPERIENCE
			assessment = Assessment.E
		}
	}

	@Transactional(readOnly = true)
	fun get(id: UUID): Level = find(id)
		?: throw ModelNotFoundException(Level::class, id)

	@Transactional(readOnly = true)
	fun find(id: UUID): Level? = levelRepository.findNullable(id)

	fun initializePlayerLevel(): Level = Level(BASE_LEVEL) {
		experienceToNextLevel =
			countExperienceService.countPlayerExperienceToNextLevel(BASE_FIRST_LEVEL)
	}

	fun initializeTopicLevel(): Level = Level(BASE_LEVEL) {
		experienceToNextLevel =
			countExperienceService.countTopicExperienceToNextLevel(BASE_FIRST_LEVEL)
	}
}
