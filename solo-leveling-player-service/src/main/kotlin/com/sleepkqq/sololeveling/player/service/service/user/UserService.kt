package com.sleepkqq.sololeveling.player.service.service.user

import com.sleepkqq.sololeveling.player.model.entity.user.User
import java.time.LocalDateTime

interface UserService {
	fun get(id: Long): User
	fun find(id: Long): User?
	fun findVersion(id: Long): Int?
	fun insert(user: User): User
	fun update(user: User, now: LocalDateTime = LocalDateTime.now()): User
	fun upsert(user: User): User
} 