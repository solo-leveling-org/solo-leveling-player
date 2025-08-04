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
	collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
	nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT,
	nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
abstract class ProtoMapper {

	@Named("toEntityTaskTopic")
	fun map(input: com.sleepkqq.sololeveling.proto.player.TaskTopic): TaskTopic =
		TaskTopic.valueOf(input.name)

	@Named("toEntityAssessment")
	fun map(input: com.sleepkqq.sololeveling.proto.player.Assessment): Assessment =
		Assessment.valueOf(input.name)

	@Named("toEntityPlayerTaskStatus")
	fun map(input: com.sleepkqq.sololeveling.proto.player.PlayerTaskStatus): PlayerTaskStatus =
		PlayerTaskStatus.valueOf(input.name)

	@Named("toEntityTaskRarity")
	fun map(input: com.sleepkqq.sololeveling.proto.player.TaskRarity): TaskRarity =
		TaskRarity.valueOf(input.name)

	@Named("toEntityUserRole")
	fun map(input: com.sleepkqq.sololeveling.proto.user.UserRole): UserRole =
		UserRole.valueOf(input.name)

	@Named("toSoulCoins")
	fun map(input: BigDecimal): Money = input.toMoney("SLCN")

	@Named("toProtoPlayerBalance")
	@Mapping(target = "balance", source = "balance", qualifiedByName = ["toSoulCoins"])
	abstract fun map(input: UserView.TargetOf_player.TargetOf_balance): com.sleepkqq.sololeveling.proto.player.PlayerBalanceView

	@Named("toProtoPlayer")
	@Mapping(target = "balance", source = "balance", qualifiedByName = ["toProtoPlayerBalance"])
	abstract fun map(input: UserView.TargetOf_player): com.sleepkqq.sololeveling.proto.player.PlayerView

	abstract fun map(input: PlayerTaskTopicView): com.sleepkqq.sololeveling.proto.player.PlayerTaskTopicView

	abstract fun map(input: PlayerTaskView): com.sleepkqq.sololeveling.proto.player.PlayerTaskView

	@Mapping(target = "player", source = "player", qualifiedByName = ["toProtoPlayer"])
	abstract fun map(input: UserView): com.sleepkqq.sololeveling.proto.user.UserView

	fun map(input: com.sleepkqq.sololeveling.proto.player.PlayerTaskInput): PlayerTaskInput =
		PlayerTaskInput(
			id = UUID.fromString(input.id),
			version = input.version,
			status = PlayerTaskStatus.valueOf(input.status.name)
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
