package com.sleepkqq.sololeveling.player.service.service.user

import com.sleepkqq.sololeveling.player.model.entity.player.Player
import com.sleepkqq.sololeveling.player.model.entity.user.User
import com.sleepkqq.sololeveling.player.service.service.player.LevelService
import org.springframework.stereotype.Service

@Service
class UserRegistrationService(
	private val levelService: LevelService
) {

	private companion object {
		const val BASE_PLAYER_MAX_TASKS = 5
	}

	fun register(user: User): User = User(user) {
		player = Player {
			id = user.id
			maxTasks = BASE_PLAYER_MAX_TASKS
			level = levelService.initializePlayerLevel()
		}
	}
}