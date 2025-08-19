package com.sleepkqq.sololeveling.player.service.service.user

import com.sleepkqq.sololeveling.player.model.entity.player.Player
import com.sleepkqq.sololeveling.player.model.entity.player.enums.LevelType
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic
import com.sleepkqq.sololeveling.player.model.entity.user.User
import com.sleepkqq.sololeveling.player.service.service.player.LevelService
import com.sleepkqq.sololeveling.player.service.service.player.PlayerBalanceService
import com.sleepkqq.sololeveling.player.service.service.player.PlayerTaskTopicService
import org.springframework.stereotype.Service

@Service
class UserRegistrationService(
	private val levelService: LevelService,
	private val playerBalanceService: PlayerBalanceService,
	private val playerTaskTopicService: PlayerTaskTopicService
) {

	private companion object {
		const val INITIAL_PLAYER_MAX_TASKS = 5
	}

	fun register(user: User): User = User(user) {
		player = Player {
			id = user.id
			maxTasks = INITIAL_PLAYER_MAX_TASKS
			level = levelService.initializeLevel(LevelType.PLAYER)
			balance = playerBalanceService.initializePlayerBalance()
			taskTopics = TaskTopic.entries.map { playerTaskTopicService.initialize(user.id, it) }
		}
	}
}
