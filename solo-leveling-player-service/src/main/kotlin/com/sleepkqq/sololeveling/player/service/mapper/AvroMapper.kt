package com.sleepkqq.sololeveling.player.service.mapper

import com.sleepkqq.sololeveling.avro.task.GenerateTask
import com.sleepkqq.sololeveling.avro.task.SaveTask
import com.sleepkqq.sololeveling.player.model.entity.task.Task
import com.sleepkqq.sololeveling.player.model.entity.task.dto.TaskInput
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskRarity
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic
import org.mapstruct.CollectionMappingStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Named
import org.mapstruct.ReportingPolicy
import org.springframework.stereotype.Component
import java.util.UUID

@Component
@Mapper(
	componentModel = "spring",
	unmappedTargetPolicy = ReportingPolicy.IGNORE,
	collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED
)
abstract class AvroMapper {

	@Named("toEntityTaskRarity")
	fun map(taskRarity: com.sleepkqq.sololeveling.avro.task.TaskRarity): TaskRarity =
		TaskRarity.valueOf(taskRarity.name)

	@Named("toAvroTaskRarity")
	fun map(taskRarity: TaskRarity): com.sleepkqq.sololeveling.avro.task.TaskRarity =
		com.sleepkqq.sololeveling.avro.task.TaskRarity.valueOf(taskRarity.name)

	@Named("toEntityTaskTopic")
	fun map(topic: com.sleepkqq.sololeveling.avro.task.TaskTopic): TaskTopic =
		TaskTopic.valueOf(topic.name)

	@Named("toAvroTaskTopic")
	fun map(topic: TaskTopic): com.sleepkqq.sololeveling.avro.task.TaskTopic =
		com.sleepkqq.sololeveling.avro.task.TaskTopic.valueOf(topic.name)

	fun map(saveTask: SaveTask): TaskInput = TaskInput(
		id = UUID.fromString(saveTask.taskId),
		title = saveTask.title,
		description = saveTask.description,
		experience = saveTask.experience,
		currencyReward = saveTask.currencyReward,
		rarity = map(saveTask.rarity),
		topics = saveTask.topics.map { map(it) },
		agility = saveTask.agility,
		strength = saveTask.strength,
		intelligence = saveTask.intelligence,
		version = saveTask.version,
	)

	@Mapping(target = "taskId", source = "task.id")
	@Mapping(target = "topics", source = "topics", qualifiedByName = ["toAvroTaskTopic"])
	@Mapping(target = "rarity", source = "rarity", qualifiedByName = ["toAvroTaskRarity"])
	abstract fun map(task: Task, topics: Collection<TaskTopic>, rarity: TaskRarity): GenerateTask
}
