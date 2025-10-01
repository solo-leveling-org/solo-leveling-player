package com.sleepkqq.sololeveling.player.service.service.user.impl

import com.sleepkqq.sololeveling.player.model.entity.Immutables
import com.sleepkqq.sololeveling.player.model.entity.user.User
import com.sleepkqq.sololeveling.player.model.entity.user.dto.UserView
import com.sleepkqq.sololeveling.player.model.repository.user.UserRepository
import com.sleepkqq.sololeveling.player.service.exception.ModelNotFoundException
import com.sleepkqq.sololeveling.player.service.service.user.UserRegistrationService
import com.sleepkqq.sololeveling.player.service.service.user.UserService
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Suppress("unused")
@Service
class UserServiceImpl(
	private val userRepository: UserRepository,
	private val userRegistrationService: UserRegistrationService
) : UserService {

	@Transactional(readOnly = true)
	override fun get(id: Long): UserView = find(id)
		?: throw ModelNotFoundException(User::class, id)

	@Transactional(readOnly = true)
	override fun find(id: Long): UserView? = userRepository.findView(id, UserView::class.java)

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
}
