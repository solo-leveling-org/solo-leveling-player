package com.sleepkqq.sololeveling.player.service.service.player.impl

import com.sleepkqq.sololeveling.player.model.entity.player.Level
import com.sleepkqq.sololeveling.player.model.entity.player.enums.Assessment
import com.sleepkqq.sololeveling.player.model.repository.player.LevelRepository
import com.sleepkqq.sololeveling.player.service.exception.ModelNotFoundException
import com.sleepkqq.sololeveling.player.service.service.player.CountExperienceService
import com.sleepkqq.sololeveling.player.service.service.player.LevelService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Suppress("unused")
@Service
class LevelServiceImpl(
	private val levelRepository: LevelRepository,
	private val countExperienceService: CountExperienceService
) : LevelService {

	companion object {
		private const val BASE_FIRST_LEVEL = 1
		private const val BASE_BEGIN_EXPERIENCE = 0

		private val BASE_LEVEL = Level {
			level = BASE_FIRST_LEVEL
			totalExperience = BASE_BEGIN_EXPERIENCE
			currentExperience = BASE_BEGIN_EXPERIENCE
			assessment = Assessment.E
		}
	}

	@Transactional(readOnly = true)
	override fun get(id: UUID): Level = find(id)
		?: throw ModelNotFoundException(Level::class, id)

	@Transactional(readOnly = true)
	override fun find(id: UUID): Level? = levelRepository.findNullable(id)

	override fun initializePlayerLevel(): Level = Level(BASE_LEVEL) {
		id = UUID.randomUUID()
		experienceToNextLevel =
			countExperienceService.countPlayerExperienceToNextLevel(BASE_FIRST_LEVEL)
	}

	override fun initializeTopicLevel(): Level = Level(BASE_LEVEL) {
		id = UUID.randomUUID()
		experienceToNextLevel =
			countExperienceService.countTopicExperienceToNextLevel(BASE_FIRST_LEVEL)
	}
}
