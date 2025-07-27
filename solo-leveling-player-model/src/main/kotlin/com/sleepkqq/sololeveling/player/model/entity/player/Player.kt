package com.sleepkqq.sololeveling.player.model.entity.player

import com.sleepkqq.sololeveling.player.model.entity.Model
import com.sleepkqq.sololeveling.player.model.entity.user.User
import org.babyfish.jimmer.sql.*

@Entity
@Table(name = "players")
interface Player : Model {

	@Id
	val id: Long

	val maxTasks: Int

	@OneToOne
	@JoinColumn(name = "user_id")
	val user: User

	@OneToOne(mappedBy = "player")
	val level: Level?

	@OneToOne(mappedBy = "player")
	val balance: PlayerBalance?

	@OneToMany(mappedBy = "player")
	val taskTopics: List<PlayerTaskTopic>
}
