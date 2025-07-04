package com.sleepkqq.sololeveling.player.model.entity.player

import com.sleepkqq.sololeveling.player.model.entity.Model
import com.sleepkqq.sololeveling.player.model.entity.player.enums.Assessment
import org.babyfish.jimmer.sql.*
import org.babyfish.jimmer.sql.meta.UUIDIdGenerator
import java.util.*

@Entity
@Table(name = "levels")
interface Level : Model {

	@Id
	@GeneratedValue(generatorType = UUIDIdGenerator::class)
	val id: UUID

	val level: Int

	val totalExperience: Int

	val currentExperience: Int

	val experienceToNextLevel: Int

	val assessment: Assessment

	@OneToOne
	@JoinColumn(name = "player_id")
	val player: Player?

	@OneToOne
	@JoinColumn(name = "player_task_topic_id")
	val playerTaskTopic: PlayerTaskTopic?
}
