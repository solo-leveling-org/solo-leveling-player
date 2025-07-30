package com.sleepkqq.sololeveling.player.service.mapper

import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerTaskInput
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerTaskTopicView
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerTaskView
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerView
import com.sleepkqq.sololeveling.player.model.entity.player.enums.Assessment
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerTaskStatus
import com.sleepkqq.sololeveling.player.model.entity.task.dto.TaskView
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskRarity
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic
import com.sleepkqq.sololeveling.player.model.entity.user.dto.UserInput
import com.sleepkqq.sololeveling.player.model.entity.user.dto.UserView
import com.sleepkqq.sololeveling.player.model.entity.user.enums.UserRole
import org.mapstruct.*
import org.springframework.stereotype.Component

@Component
@Mapper(
	componentModel = "spring",
	unmappedTargetPolicy = ReportingPolicy.IGNORE,
	collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED
)
abstract class ProtoMapper : BaseMapper() {

	@Named("toEntityTaskTopic")
	fun map(taskTopic: com.sleepkqq.sololeveling.proto.player.TaskTopic): TaskTopic =
		TaskTopic.valueOf(taskTopic.name)

	@Named("toProtoTaskTopic")
	fun map(taskTopic: TaskTopic): com.sleepkqq.sololeveling.proto.player.TaskTopic =
		com.sleepkqq.sololeveling.proto.player.TaskTopic.valueOf(taskTopic.name)

	@Named("toProtoAssessment")
	fun map(assessment: Assessment): com.sleepkqq.sololeveling.proto.player.Assessment =
		com.sleepkqq.sololeveling.proto.player.Assessment.valueOf(assessment.name)

	@Named("toEntityAssessment")
	fun map(assessment: com.sleepkqq.sololeveling.proto.player.Assessment): Assessment =
		Assessment.valueOf(assessment.name)

	@Named("toProtoPlayerTaskStatus")
	fun map(playerTaskStatus: PlayerTaskStatus): com.sleepkqq.sololeveling.proto.player.PlayerTaskStatus =
		com.sleepkqq.sololeveling.proto.player.PlayerTaskStatus.valueOf(playerTaskStatus.name)

	@Named("toEntityPlayerTaskStatus")
	fun map(playerTaskStatus: com.sleepkqq.sololeveling.proto.player.PlayerTaskStatus): PlayerTaskStatus =
		PlayerTaskStatus.valueOf(
			playerTaskStatus.name
		)

	@Named("toProtoTaskRarity")
	fun map(taskRarity: TaskRarity): com.sleepkqq.sololeveling.proto.player.TaskRarity =
		com.sleepkqq.sololeveling.proto.player.TaskRarity.valueOf(taskRarity.name)

	@Named("toEntityTaskRarity")
	fun map(taskRarity: com.sleepkqq.sololeveling.proto.player.TaskRarity): TaskRarity =
		TaskRarity.valueOf(taskRarity.name)

	@Named("toProtoUserRole")
	fun map(userRole: UserRole): com.sleepkqq.sololeveling.proto.user.UserRole =
		com.sleepkqq.sololeveling.proto.user.UserRole.valueOf(userRole.name)

	@Named("toEntityUserRole")
	fun map(userRole: com.sleepkqq.sololeveling.proto.user.UserRole): UserRole =
		UserRole.valueOf(userRole.name)

	@Named("toProtoPlayerBalance")
	@Mapping(target = "id", source = "id", qualifiedByName = ["uuidToString"])
	@Mapping(target = "balance", source = "balance", qualifiedByName = ["bigDecimalToString"])
	abstract fun map(playerBalance: PlayerView.TargetOf_balance): com.sleepkqq.sololeveling.proto.player.PlayerBalanceView

	@Named("toProtoLevel")
	@Mapping(target = "id", source = "id", qualifiedByName = ["uuidToString"])
	abstract fun map(level: PlayerView.TargetOf_level): com.sleepkqq.sololeveling.proto.player.LevelView

	@Mapping(target = "balance", source = "balance", qualifiedByName = ["toProtoPlayerBalance"])
	@Mapping(target = "level", source = "level", qualifiedByName = ["toProtoLevel"])
	abstract fun map(playerView: PlayerView): com.sleepkqq.sololeveling.proto.player.PlayerView

	@Mapping(target = "id", source = "id", qualifiedByName = ["uuidToString"])
	@Mapping(target = "taskTopic", source = "taskTopic", qualifiedByName = ["toProtoTaskTopic"])
	abstract fun map(playerTaskTopicView: PlayerTaskTopicView): com.sleepkqq.sololeveling.proto.player.PlayerTaskTopicView

	@Mapping(target = "id", source = "id", qualifiedByName = ["uuidToString"])
	@Mapping(target = "rarity", source = "rarity", qualifiedByName = ["toProtoTaskRarity"])
	abstract fun map(taskView: TaskView): com.sleepkqq.sololeveling.proto.player.TaskView

	@Mapping(target = "id", source = "id", qualifiedByName = ["uuidToString"])
	abstract fun map(playerTaskView: PlayerTaskView): com.sleepkqq.sololeveling.proto.player.PlayerTaskView

	fun map(playerTaskInput: com.sleepkqq.sololeveling.proto.player.PlayerTaskInput): PlayerTaskInput =
		PlayerTaskInput(
			id = map(playerTaskInput.id),
			version = playerTaskInput.version,
			status = map(playerTaskInput.status)
		)

	@Mapping(target = "rolesList", source = "roles", qualifiedByName = ["toProtoUserRole"])
	abstract fun map(userView: UserView): com.sleepkqq.sololeveling.proto.user.UserView

	fun map(userInput: com.sleepkqq.sololeveling.proto.user.UserInput): UserInput =
		UserInput(
			id = userInput.id,
			username = userInput.username,
			firstName = userInput.firstName,
			lastName = userInput.lastName,
			photoUrl = userInput.photoUrl,
			locale = userInput.locale,
			roles = userInput.rolesList.map { map(it) }
		)
}
