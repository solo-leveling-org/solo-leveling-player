package com.sleepkqq.sololeveling.player.model.entity.player

import com.sleepkqq.sololeveling.player.model.entity.Model
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerTaskStatus
import com.sleepkqq.sololeveling.player.model.entity.task.Task
import org.babyfish.jimmer.sql.*
import org.babyfish.jimmer.sql.meta.UUIDIdGenerator
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "player_tasks")
@KeyUniqueConstraint
interface PlayerTask : Model {

	@Id
	@GeneratedValue(generatorType = UUIDIdGenerator::class)
	val id: UUID

	val status: PlayerTaskStatus

	val order: Int

	val closedAt: LocalDateTime?

	@ManyToOne
	@JoinColumn(name = "player_id")
	val player: Player

	@ManyToOne
	@JoinColumn(name = "task_id")
	val task: Task
}
