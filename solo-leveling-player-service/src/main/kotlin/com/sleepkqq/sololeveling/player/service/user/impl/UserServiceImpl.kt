package com.sleepkqq.sololeveling.player.service.user.impl

import com.sleepkqq.sololeveling.player.model.entity.Fetchers
import com.sleepkqq.sololeveling.player.model.entity.Immutables
import com.sleepkqq.sololeveling.player.model.entity.player.enums.LevelType
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic
import com.sleepkqq.sololeveling.player.model.entity.user.User
import com.sleepkqq.sololeveling.player.model.entity.user.UserFetcher
import com.sleepkqq.sololeveling.player.model.entity.user.enums.UserRole
import com.sleepkqq.sololeveling.player.model.repository.user.UserRepository
import com.sleepkqq.sololeveling.player.service.notification.NotificationCommand
import com.sleepkqq.sololeveling.player.service.notification.NotificationService
import com.sleepkqq.sololeveling.player.service.player.LevelService
import com.sleepkqq.sololeveling.player.service.player.PlayerBalanceService
import com.sleepkqq.sololeveling.player.service.player.PlayerTaskTopicService
import com.sleepkqq.sololeveling.player.service.user.UserService
import org.babyfish.jimmer.View
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Locale
import java.util.UUID
import kotlin.reflect.KClass

@Service
class UserServiceImpl(
	private val userRepository: UserRepository,
	private val notificationService: NotificationService,
	private val levelService: LevelService,
	private val playerBalanceService: PlayerBalanceService,
	private val playerTaskTopicService: PlayerTaskTopicService
) : UserService {

	private companion object {
		const val INITIAL_PLAYER_MAX_TASKS = 5
	}

	override fun find(id: Long, fetcher: UserFetcher): User? =
		userRepository.findNullable(id, fetcher)

	override fun <V : View<User>> findView(id: Long, viewType: KClass<V>): V? =
		userRepository.findView(id, viewType.java)

	@Transactional
	override fun insert(user: User): User = userRepository.save(user, SaveMode.INSERT_ONLY)

	@Transactional
	override fun update(user: User): User =
		userRepository.save(user, SaveMode.UPDATE_ONLY)

	@Transactional
	override fun upsert(user: User): User =
		find(
			user.id(),
			Fetchers.USER_FETCHER
				.version()
				.manualLocale()
				.roles(Fetchers.USER_ROLE_ITEM_FETCHER.allScalarFields())
		)
			?.let {
				update(
					Immutables.createUser(user) { u ->
						u.setVersion(it.version())
						u.setManualLocale(it.manualLocale())
						u.setRoles(it.roles())
					}
				)
			}
			?: insert(register(user))

	@Transactional
	override fun updateLocale(id: Long, locale: Locale) {
		userRepository.updateLocale(id, locale)

		notificationService.send(NotificationCommand.UpdateLocale(id))
	}

	override fun register(user: User): User = Immutables.createUser(user) {
		it.setRoles(
			listOf(
				Immutables.createUserRoleItem { r ->
					r.setId(UUID.randomUUID())
					r.setRole(UserRole.USER)
				}
			)
		)
		it.setPlayer(Immutables.createPlayer { p ->
			p.setId(user.id())
			p.setMaxTasks(INITIAL_PLAYER_MAX_TASKS)
			p.setLevel(levelService.initializeLevel(LevelType.PLAYER))
			p.setBalance(playerBalanceService.initializePlayerBalance())
			p.setTaskTopics(
				TaskTopic.entries.map { topic ->
					playerTaskTopicService.initialize(user.id(), topic)
				}
			)
		})
	}
}
