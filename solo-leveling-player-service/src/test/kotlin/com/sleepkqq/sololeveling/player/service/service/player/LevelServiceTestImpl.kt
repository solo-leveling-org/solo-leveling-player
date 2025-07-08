package com.sleepkqq.sololeveling.player.service.service.player

import com.sleepkqq.sololeveling.player.model.entity.player.Level
import com.sleepkqq.sololeveling.player.model.entity.player.enums.Assessment
import com.sleepkqq.sololeveling.player.service.exception.ModelNotFoundException
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Service
@Profile("test")
class LevelServiceTestImpl : LevelService {
	private val levels = ConcurrentHashMap<UUID, Level>()

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

	override fun get(id: UUID): Level = find(id) ?: throw ModelNotFoundException(Level::class, id)

	override fun find(id: UUID): Level? = levels[id]

	override fun initializePlayerLevel(): Level = Level(BASE_LEVEL) {
		id = UUID.randomUUID()
		experienceToNextLevel = 100 // Default value for testing
	}

	override fun initializeTopicLevel(): Level = Level(BASE_LEVEL) {
		id = UUID.randomUUID()
		experienceToNextLevel = 100 // Default value for testing
	}

	fun clear() = levels.clear()
} 