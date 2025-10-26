package com.sleepkqq.sololeveling.player.service.user

import com.sleepkqq.sololeveling.player.model.entity.Immutables
import com.sleepkqq.sololeveling.player.model.entity.player.enums.LevelType
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic
import com.sleepkqq.sololeveling.player.model.entity.user.User
import com.sleepkqq.sololeveling.player.model.entity.user.enums.UserRole
import com.sleepkqq.sololeveling.player.service.player.LevelService
import com.sleepkqq.sololeveling.player.service.player.PlayerBalanceService
import com.sleepkqq.sololeveling.player.service.player.PlayerTaskTopicService
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

	fun register(user: User): User = Immutables.createUser(user) {
		it.setRoles(setOf(UserRole.USER))
		it.setPlayer(Immutables.createPlayer { p ->
			p.setId(user.id())
			p.setMaxTasks(INITIAL_PLAYER_MAX_TASKS)
			p.setLevel(levelService.initializeLevel(LevelType.PLAYER))
			p.setBalance(playerBalanceService.initializePlayerBalance())
			p.setTaskTopics(
				TaskTopic.entries.map { t ->
					playerTaskTopicService.initialize(
						user.id(),
						t
					)
				}
			)
		})
	}
}
