package com.sleepkqq.sololeveling.player.service.service.user

import com.sleepkqq.sololeveling.player.model.entity.user.User
import com.sleepkqq.sololeveling.player.model.entity.user.dto.UserView
import java.time.LocalDateTime

interface UserService {
	fun get(id: Long): UserView
	fun find(id: Long): UserView?
	fun findVersion(id: Long): Int?
	fun insert(user: User): User
	fun update(user: User, now: LocalDateTime = LocalDateTime.now()): User
	fun upsert(user: User): User
}
