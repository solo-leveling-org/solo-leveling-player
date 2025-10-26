package com.sleepkqq.sololeveling.player.mapper

import com.sleepkqq.sololeveling.avro.task.GenerateTask
import com.sleepkqq.sololeveling.avro.task.SaveTask
import com.sleepkqq.sololeveling.player.model.entity.task.Task
import com.sleepkqq.sololeveling.player.model.entity.task.dto.TaskInput
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskRarity
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic
import org.mapstruct.CollectionMappingStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.ReportingPolicy

@Mapper(
	componentModel = "spring",
	unmappedTargetPolicy = ReportingPolicy.IGNORE,
	collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED
)
abstract class AvroMapper {

	fun map(taskRarity: com.sleepkqq.sololeveling.avro.task.TaskRarity): TaskRarity =
		TaskRarity.valueOf(taskRarity.name)

	fun map(taskRarity: TaskRarity): com.sleepkqq.sololeveling.avro.task.TaskRarity =
		com.sleepkqq.sololeveling.avro.task.TaskRarity.valueOf(taskRarity.name)

	fun map(topic: com.sleepkqq.sololeveling.avro.task.TaskTopic): TaskTopic =
		TaskTopic.valueOf(topic.name)

	fun map(topic: TaskTopic): com.sleepkqq.sololeveling.avro.task.TaskTopic =
		com.sleepkqq.sololeveling.avro.task.TaskTopic.valueOf(topic.name)

	@Mapping(target = "id", source = "taskId")
	abstract fun map(saveTask: SaveTask): TaskInput

	@Mapping(target = "taskId", expression = "java(task.id().toString())")
	@Mapping(target = "version", expression = "java(task.version())")
	abstract fun map(task: Task, topics: Collection<TaskTopic>, rarity: TaskRarity): GenerateTask
}
