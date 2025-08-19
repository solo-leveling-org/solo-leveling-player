package com.sleepkqq.sololeveling.player.service.mapper

import com.google.protobuf.Timestamp
import com.google.type.Money
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerTaskInput
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerTaskTopicInput
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerTaskTopicView
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerTaskView
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerView
import com.sleepkqq.sololeveling.player.model.entity.player.enums.Assessment
import com.sleepkqq.sololeveling.player.model.entity.player.enums.CurrencyCode
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerTaskStatus
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskRarity
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic
import com.sleepkqq.sololeveling.player.model.entity.user.dto.UserInput
import com.sleepkqq.sololeveling.player.model.entity.user.dto.UserView
import com.sleepkqq.sololeveling.player.model.entity.user.enums.UserRole
import com.sleepkqq.sololeveling.player.service.extenstions.toMoney
import com.sleepkqq.sololeveling.player.service.extenstions.toTimestamp
import org.mapstruct.*
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Suppress("unused")
@Component
@Mapper(
	componentModel = "spring",
	unmappedTargetPolicy = ReportingPolicy.IGNORE,
	collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
	nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT,
	nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
abstract class ProtoMapper {

	fun map(input: LocalDateTime): Timestamp = input.toTimestamp()

	@Mapping(target = "isActive", source = "active")
	abstract fun map(input: PlayerTaskTopicView): com.sleepkqq.sololeveling.proto.player.PlayerTaskTopicView

	@Mapping(target = "task.topicsList", source = "input.task.topics")
	abstract fun map(input: PlayerTaskView): com.sleepkqq.sololeveling.proto.player.PlayerTaskView

	fun map(balance: BigDecimal, currencyCode: CurrencyCode): Money = balance.toMoney(currencyCode)

	@Mapping(
		target = "player.balance.balance",
		expression = "java(map(targetOf_balance.getBalance(), targetOf_balance.getCurrencyCode()))"
	)
	abstract fun map(input: UserView): com.sleepkqq.sololeveling.proto.user.UserView

	@Mapping(
		target = "balance.balance",
		expression = "java(map(targetOf_balance.getBalance(), targetOf_balance.getCurrencyCode()))"
	)
	@Mapping(target = "taskTopicsList", source = "taskTopics")
	abstract fun map(input: PlayerView): com.sleepkqq.sololeveling.proto.player.PlayerView

	fun map(input: com.sleepkqq.sololeveling.proto.player.PlayerTaskInput): PlayerTaskInput =
		PlayerTaskInput(
			id = UUID.fromString(input.id),
			version = input.version,
			order = input.order,
			status = PlayerTaskStatus.valueOf(input.status.name),
			task = map(input.task)
		)

	fun map(input: com.sleepkqq.sololeveling.proto.player.TaskInput): PlayerTaskInput.TargetOf_task =
		PlayerTaskInput.TargetOf_task(
			id = UUID.fromString(input.id),
			version = input.version,
			title = input.title,
			description = input.description,
			experience = input.experience,
			currencyReward = input.currencyReward,
			rarity = TaskRarity.valueOf(input.rarity.name),
			topics = input.topicsList.map { TaskTopic.valueOf(it.name) }.toSet(),
			agility = input.agility,
			strength = input.strength,
			intelligence = input.intelligence
		)

	fun map(input: com.sleepkqq.sololeveling.proto.user.UserInput): UserInput =
		UserInput(
			id = input.id,
			username = input.username,
			firstName = input.firstName,
			lastName = input.lastName,
			photoUrl = input.photoUrl,
			locale = input.locale,
			roles = input.rolesList.map { UserRole.valueOf(it.name) }
		)

	fun map(input: com.sleepkqq.sololeveling.proto.player.PlayerTaskTopicInput): PlayerTaskTopicInput =
		PlayerTaskTopicInput(
			id = UUID.fromString(input.id),
			version = input.version,
			taskTopic = TaskTopic.valueOf(input.taskTopic.name),
			isActive = input.isActive,
			level = map(input.level)
		)

	fun map(input: com.sleepkqq.sololeveling.proto.player.LevelInput): PlayerTaskTopicInput.TargetOf_level =
		PlayerTaskTopicInput.TargetOf_level(
			id = UUID.fromString(input.id),
			version = input.version,
			level = input.level,
			totalExperience = input.totalExperience,
			currentExperience = input.currentExperience,
			experienceToNextLevel = input.experienceToNextLevel,
			assessment = Assessment.valueOf(input.assessment.name),
		)
}
