package com.sleepkqq.sololeveling.player.service.service.user

import com.sleepkqq.sololeveling.player.model.entity.user.User
import com.sleepkqq.sololeveling.player.model.repository.user.UserRepository
import com.sleepkqq.sololeveling.player.service.exception.ModelNotFoundException
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class UserService(
	private val userRepository: UserRepository,
	private val userRegistrationService: UserRegistrationService
) {

	@Transactional(readOnly = true)
	fun get(id: Long): User = find(id)
		?: throw ModelNotFoundException(User::class, id)

	@Transactional(readOnly = true)
	fun find(id: Long): User? = userRepository.findNullable(id)

	@Transactional(readOnly = true)
	fun findVersion(id: Long): Int? = userRepository.findVersionById(id)

	@Transactional
	fun insert(user: User): User = userRepository.save(user, SaveMode.INSERT_ONLY)

	@Transactional
	fun update(user: User, now: LocalDateTime): User = userRepository.save(
		User(user) { updatedAt = now },
		SaveMode.UPDATE_ONLY
	)

	@Transactional
	fun update(user: User): User = update(user, LocalDateTime.now())

	@Transactional
	fun upsert(user: User): User {
		val now = LocalDateTime.now()
		return findVersion(user.id)
			?.let {
				User(user) {
					version = it
					lastLoginAt = now
				}
			}
			?: insert(userRegistrationService.register(user))
	}
}