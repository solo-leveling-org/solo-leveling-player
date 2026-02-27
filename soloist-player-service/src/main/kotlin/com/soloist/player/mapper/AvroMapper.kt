package com.soloist.player.mapper

import com.soloist.avro.task.Task
import com.soloist.avro.task.TaskTopic
import com.soloist.player.model.entity.player.TaskTopicItem
import com.soloist.player.model.entity.task.dto.GenerateTaskView
import com.soloist.player.model.entity.task.dto.SaveTaskInput
import org.babyfish.jimmer.View
import org.mapstruct.*

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

	abstract fun map(input: GenerateTaskView): Task

	abstract fun map(input: TaskTopic): com.soloist.player.model.entity.task.enums.TaskTopic

	abstract fun map(input: Task): SaveTaskInput
}
