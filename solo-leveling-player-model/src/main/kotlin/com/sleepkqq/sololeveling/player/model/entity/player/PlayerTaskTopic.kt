package com.sleepkqq.sololeveling.player.model.entity.player

import com.sleepkqq.sololeveling.player.model.entity.Model
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic
import org.babyfish.jimmer.sql.*
import org.babyfish.jimmer.sql.meta.UUIDIdGenerator
import java.util.*

@Entity
@Table(name = "player_task_topics")
interface PlayerTaskTopic : Model {

	@Id
	@GeneratedValue(generatorType = UUIDIdGenerator::class)
	val id: UUID

	val taskTopic: TaskTopic

	@ManyToOne
	@JoinColumn(name = "player_id")
	val player: Player

	@OneToOne(mappedBy = "playerTaskTopic")
	val level: Level?
}