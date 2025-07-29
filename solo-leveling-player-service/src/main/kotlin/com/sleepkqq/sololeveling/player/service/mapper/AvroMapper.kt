package com.sleepkqq.sololeveling.player.service.mapper

import com.sleepkqq.sololeveling.avro.task.SaveTask
import com.sleepkqq.sololeveling.player.model.entity.task.dto.TaskInput
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskRarity
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic
import com.sleepkqq.sololeveling.player.service.extenstions.toBigDecimal
import org.mapstruct.CollectionMappingStrategy
import org.mapstruct.Mapper
import org.mapstruct.Named
import org.mapstruct.ReportingPolicy
import org.springframework.stereotype.Component

@Component
@Mapper(
	componentModel = "spring",
	unmappedTargetPolicy = ReportingPolicy.IGNORE,
	collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED
)
class AvroMapper : BaseMapper() {

	fun map(saveTask: SaveTask): TaskInput = TaskInput(
		id = map(saveTask.taskId),
		title = saveTask.title,
		description = saveTask.description,
		experience = saveTask.experience,
		currencyReward = saveTask.currencyReward.toBigDecimal(),
		rarity = map(saveTask.rarity),
		topics = saveTask.topics.map { map(it) },
		agility = saveTask.agility,
		strength = saveTask.strength,
		intelligence = saveTask.intelligence,
		version = saveTask.version,
	)

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
}
