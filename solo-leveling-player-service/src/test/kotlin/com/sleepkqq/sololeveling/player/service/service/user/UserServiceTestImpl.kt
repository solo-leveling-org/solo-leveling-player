package com.sleepkqq.sololeveling.player.service.service.user

import com.sleepkqq.sololeveling.player.model.entity.user.User
import com.sleepkqq.sololeveling.player.service.exception.ModelNotFoundException
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

@Service
@Profile("test")
class UserServiceTestImpl : UserService {

	private val users = ConcurrentHashMap<Long, User>()

	override fun get(id: Long): User = find(id) ?: throw ModelNotFoundException(User::class, id)

	override fun find(id: Long): User? = users[id]

	override fun findVersion(id: Long): Int? = users[id]?.version

	override fun insert(user: User): User {
		if (users.containsKey(user.id)) {
			throw IllegalStateException("User with id ${user.id} already exists")
		}
		users[user.id] = user
		return user
	}

	override fun update(user: User, now: LocalDateTime): User {
		if (!users.containsKey(user.id)) {
			throw IllegalStateException("User with id ${user.id} not found")
		}
		val updated = User(user) { updatedAt = now }
		users[user.id] = updated
		return updated
	}

	override fun upsert(user: User): User {
		val now = LocalDateTime.now()
		return findVersion(user.id)
			?.let {
				update(
					User(user) {
						version = it
						lastLoginAt = now
					},
					now
				)
			}
			?: insert(user)
	}

	fun clear() = users.clear()
} 