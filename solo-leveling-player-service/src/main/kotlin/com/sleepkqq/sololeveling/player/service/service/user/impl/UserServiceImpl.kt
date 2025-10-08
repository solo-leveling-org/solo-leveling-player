package com.sleepkqq.sololeveling.player.service.service.user.impl

import com.sleepkqq.sololeveling.player.model.entity.Immutables
import com.sleepkqq.sololeveling.player.model.entity.user.User
import com.sleepkqq.sololeveling.player.model.entity.user.UserFetcher
import com.sleepkqq.sololeveling.player.model.repository.user.UserRepository
import com.sleepkqq.sololeveling.player.service.service.notification.NotificationCommand
import com.sleepkqq.sololeveling.player.service.service.notification.NotificationService
import com.sleepkqq.sololeveling.player.service.service.user.UserRegistrationService
import com.sleepkqq.sololeveling.player.service.service.user.UserService
import org.babyfish.jimmer.View
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*
import kotlin.reflect.KClass

@Suppress("unused")
@Service
class UserServiceImpl(
	private val userRepository: UserRepository,
	private val userRegistrationService: UserRegistrationService,
	private val notificationService: NotificationService
) : UserService {

	private val log = LoggerFactory.getLogger(javaClass)

	override fun find(id: Long, fetcher: UserFetcher): User? =
		userRepository.findNullable(id, fetcher)

	override fun <V : View<User>> findView(id: Long, viewType: KClass<V>): V? =
		userRepository.findView(id, viewType.java)

	@Transactional(readOnly = true)
	override fun findVersion(id: Long): Int? = userRepository.findVersionById(id)

	@Transactional
	override fun insert(user: User): User = userRepository.save(user, SaveMode.INSERT_ONLY)

	@Transactional
	override fun update(user: User, now: LocalDateTime): User =
		userRepository.save(
			Immutables.createUser(user) {
				it.setUpdatedAt(now)
			},
			SaveMode.UPDATE_ONLY
		)

	@Transactional
	override fun upsert(user: User): User {
		val now = LocalDateTime.now()
		return findVersion(user.id())
			?.let {
				update(
					Immutables.createUser(user) { u ->
						u.setVersion(it)
						u.setLastLoginAt(now)
					},
					now
				)
			}
			?: insert(userRegistrationService.register(user))
	}

	@Transactional
	override fun updateLocale(id: Long, locale: Locale) {
		userRepository.updateLocale(id, locale)

		notificationService.send(NotificationCommand.UpdateLocale(id))
	}
}
