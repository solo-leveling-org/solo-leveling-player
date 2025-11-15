package com.sleepkqq.sololeveling.player.mapper

import com.sleepkqq.sololeveling.avro.task.GenerateTask
import com.sleepkqq.sololeveling.avro.task.SaveTask
import com.sleepkqq.sololeveling.player.model.entity.player.enums.Rarity
import com.sleepkqq.sololeveling.player.model.entity.task.Task
import com.sleepkqq.sololeveling.player.model.entity.task.dto.TaskInput
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic
import org.mapstruct.CollectionMappingStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.ReportingPolicy
import java.util.UUID

@Mapper(
	componentModel = "spring",
	unmappedTargetPolicy = ReportingPolicy.IGNORE,
	collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED
)
abstract class AvroMapper {

	fun map(rarity: com.sleepkqq.sololeveling.avro.player.Rarity): Rarity =
		Rarity.valueOf(rarity.name)

	fun map(rarity: Rarity): com.sleepkqq.sololeveling.avro.player.Rarity =
		com.sleepkqq.sololeveling.avro.player.Rarity.valueOf(rarity.name)

	fun map(topic: com.sleepkqq.sololeveling.avro.task.TaskTopic): TaskInput.TargetOf_topics =
		TaskInput.TargetOf_topics.Builder()
			.id(UUID.randomUUID())
			.topic(TaskTopic.valueOf(topic.name))
			.build()

	fun map(topic: TaskTopic): com.sleepkqq.sololeveling.avro.task.TaskTopic =
		com.sleepkqq.sololeveling.avro.task.TaskTopic.valueOf(topic.name)

	@Mapping(target = "id", source = "taskId")
	abstract fun map(input: SaveTask): TaskInput

	@Mapping(target = "taskId", expression = "java(task.id().toString())")
	@Mapping(target = "version", expression = "java(task.version())")
	abstract fun map(task: Task, topics: Collection<TaskTopic>, rarity: Rarity): GenerateTask
}
