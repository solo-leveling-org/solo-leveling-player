package com.sleepkqq.sololeveling.player.model.entity.task

import com.sleepkqq.sololeveling.player.model.entity.Model
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTask
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskRarity
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic
import org.babyfish.jimmer.sql.*
import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(name = "tasks")
interface Task : Model {

	@Id
	val id: UUID

	val title: String?

	val description: String?

	val experience: Int?

	val currencyReward: BigDecimal?

	val rarity: TaskRarity?

	val agility: Int?

	val strength: Int?

	val intelligence: Int?

	@Serialized
	val topics: List<TaskTopic>?

	@OneToMany(mappedBy = "task")
	val playerTasks: List<PlayerTask>
}
