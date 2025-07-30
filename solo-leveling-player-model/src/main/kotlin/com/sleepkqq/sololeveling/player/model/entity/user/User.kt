package com.sleepkqq.sololeveling.player.model.entity.user

import com.sleepkqq.sololeveling.player.model.entity.Model
import com.sleepkqq.sololeveling.player.model.entity.player.Player
import com.sleepkqq.sololeveling.player.model.entity.user.enums.UserRole
import org.babyfish.jimmer.sql.*
import java.time.LocalDateTime

@Entity
@Table(name = "users")
@KeyUniqueConstraint
interface User : Model {
	@Id
	val id: Long

	val username: String

	val firstName: String

	val lastName: String

	val photoUrl: String

	val locale: String

	val lastLoginAt: LocalDateTime

	@Serialized
	val roles: List<UserRole>?

	@OneToOne(mappedBy = "user")
	val player: Player?
}