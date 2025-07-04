package com.sleepkqq.sololeveling.player.service.mapper

import com.sleepkqq.sololeveling.avro.task.SaveTask
import com.sleepkqq.sololeveling.avro.task.TaskTopic
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskRarity
import com.sleepkqq.sololeveling.player.model.entity.task.Task
import org.springframework.stereotype.Component

@Component
class AvroMapper : BaseMapper() {

	fun map(saveTask: SaveTask): Task = Task {
		id = map(saveTask.taskId)
		title = saveTask.title
		description = saveTask.description
		experience = saveTask.experience
		rarity = map(saveTask.rarity)
		topics = saveTask.topics.map { map(it) }
		agility = saveTask.agility
		strength = saveTask.strength
		intelligence = saveTask.intelligence
	}

	fun map(taskRarity: com.sleepkqq.sololeveling.avro.task.TaskRarity): TaskRarity {
		return TaskRarity.valueOf(taskRarity.name)
	}

	fun map(topic: TaskTopic): com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic {
		return com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic.valueOf(topic.name)
	}

	fun map(taskRarity: TaskRarity): com.sleepkqq.sololeveling.avro.task.TaskRarity {
		return com.sleepkqq.sololeveling.avro.task.TaskRarity.valueOf(taskRarity.name)
	}

	fun map(topic: com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic): TaskTopic {
		return TaskTopic.valueOf(topic.name)
	}
}
