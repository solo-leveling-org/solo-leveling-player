package com.sleepkqq.sololeveling.player.model.entity.task

import com.sleepkqq.sololeveling.player.model.entity.Model
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTask
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskRarity
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic
import org.babyfish.jimmer.sql.*
import org.babyfish.jimmer.sql.meta.UUIDIdGenerator
import java.util.UUID

@Entity
@Table(name = "tasks")
@KeyUniqueConstraint
interface Task : Model {

	@Id
	@GeneratedValue(generatorType = UUIDIdGenerator::class)
	val id: UUID

	val title: String?

	val description: String?

	val experience: Int?

	val currencyReward: Int?

	val rarity: TaskRarity?

	val agility: Int?

	val strength: Int?

	val intelligence: Int?

	@Serialized
	val topics: Set<TaskTopic>?

	@OneToMany(mappedBy = "task")
	val playerTasks: List<PlayerTask>
}
