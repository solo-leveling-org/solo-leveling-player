package com.soloist.player.mapper

import com.soloist.avro.task.Task
import com.soloist.avro.task.TaskTopic
import com.soloist.player.model.entity.player.TaskTopicItem
import com.soloist.player.model.entity.task.dto.GenerateTaskView
import com.soloist.player.model.entity.task.dto.SaveTaskInput
import org.babyfish.jimmer.View
import org.mapstruct.CollectionMappingStrategy
import org.mapstruct.Mapper
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

	fun map(input: View<TaskTopicItem>): TaskTopic =
		TaskTopic.valueOf(input.toEntity().topic().name)

	abstract fun map(input: Task): SaveTaskInput

	abstract fun map(input: GenerateTaskView): Task
}
