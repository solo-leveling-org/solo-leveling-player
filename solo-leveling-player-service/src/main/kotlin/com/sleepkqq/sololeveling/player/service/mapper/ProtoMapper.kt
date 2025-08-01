package com.sleepkqq.sololeveling.player.service.mapper

import com.google.type.Money
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerTaskInput
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerTaskTopicView
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerTaskView
import com.sleepkqq.sololeveling.player.model.entity.player.enums.Assessment
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerTaskStatus
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskRarity
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic
import com.sleepkqq.sololeveling.player.model.entity.user.dto.UserInput
import com.sleepkqq.sololeveling.player.model.entity.user.dto.UserView
import com.sleepkqq.sololeveling.player.model.entity.user.enums.UserRole
import com.sleepkqq.sololeveling.player.service.extenstions.toMoney
import org.mapstruct.*
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.UUID

@Suppress("unused")
@Component
@Mapper(
	componentModel = "spring",
	unmappedTargetPolicy = ReportingPolicy.IGNORE,
	collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED
)
abstract class ProtoMapper {

	@Named("toEntityTaskTopic")
	fun map(taskTopic: com.sleepkqq.sololeveling.proto.player.TaskTopic): TaskTopic =
		TaskTopic.valueOf(taskTopic.name)

	@Named("toEntityAssessment")
	fun map(assessment: com.sleepkqq.sololeveling.proto.player.Assessment): Assessment =
		Assessment.valueOf(assessment.name)

	@Named("toEntityPlayerTaskStatus")
	fun map(playerTaskStatus: com.sleepkqq.sololeveling.proto.player.PlayerTaskStatus): PlayerTaskStatus =
		PlayerTaskStatus.valueOf(
			playerTaskStatus.name
		)

	@Named("toEntityTaskRarity")
	fun map(taskRarity: com.sleepkqq.sololeveling.proto.player.TaskRarity): TaskRarity =
		TaskRarity.valueOf(taskRarity.name)

	@Named("toEntityUserRole")
	fun map(userRole: com.sleepkqq.sololeveling.proto.user.UserRole): UserRole =
		UserRole.valueOf(userRole.name)

	@Named("toSoulCoins")
	fun map(input: BigDecimal): Money = input.toMoney("SLCN")

	@Named("toProtoPlayerBalance")
	@Mapping(target = "balance", source = "balance", qualifiedByName = ["toSoulCoins"])
	abstract fun map(playerBalance: UserView.TargetOf_player.TargetOf_balance): com.sleepkqq.sololeveling.proto.player.PlayerBalanceView

	@Named("toProtoPlayer")
	@Mapping(target = "balance", source = "balance", qualifiedByName = ["toProtoPlayerBalance"])
	abstract fun map(input: UserView.TargetOf_player): com.sleepkqq.sololeveling.proto.player.PlayerView

	abstract fun map(playerTaskTopicView: PlayerTaskTopicView): com.sleepkqq.sololeveling.proto.player.PlayerTaskTopicView

	abstract fun map(playerTaskView: PlayerTaskView): com.sleepkqq.sololeveling.proto.player.PlayerTaskView

	@Mapping(target = "player", source = "player", qualifiedByName = ["toProtoPlayer"])
	abstract fun map(userView: UserView): com.sleepkqq.sololeveling.proto.user.UserView

	fun map(playerTaskInput: com.sleepkqq.sololeveling.proto.player.PlayerTaskInput): PlayerTaskInput =
		PlayerTaskInput(
			id = UUID.fromString(playerTaskInput.id),
			version = playerTaskInput.version,
			status = PlayerTaskStatus.valueOf(playerTaskInput.status.name)
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
}
