package com.sleepkqq.sololeveling.player.mapper

import com.sleepkqq.sololeveling.avro.task.GenerateTask
import com.sleepkqq.sololeveling.avro.task.SaveTask
import com.sleepkqq.sololeveling.player.model.entity.player.TaskTopicItem
import com.sleepkqq.sololeveling.player.model.entity.task.dto.GenerateTaskView
import com.sleepkqq.sololeveling.player.model.entity.task.dto.SaveTaskInput
import org.babyfish.jimmer.View
import org.mapstruct.CollectionMappingStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.NullValueCheckStrategy
import org.mapstruct.NullValueMappingStrategy
import org.mapstruct.NullValuePropertyMappingStrategy
import org.mapstruct.ReportingPolicy

@Mapper(
	componentModel = "spring",
	unmappedTargetPolicy = ReportingPolicy.IGNORE,
	collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
	nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT,
	nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
abstract class AvroMapper {

	fun map(input: View<TaskTopicItem>): com.sleepkqq.sololeveling.avro.task.TaskTopic =
		com.sleepkqq.sololeveling.avro.task.TaskTopic.valueOf(input.toEntity().topic().name)

	@Mapping(target = "id", source = "taskId")
	abstract fun map(input: SaveTask): SaveTaskInput

	@Mapping(target = "taskId", source = "id")
	abstract fun map(input: GenerateTaskView): GenerateTask
}
