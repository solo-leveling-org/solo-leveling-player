package com.sleepkqq.sololeveling.player.service.mapper

import com.google.protobuf.Timestamp
import com.google.type.Money
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerBalanceTransactionView
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerBalanceView
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
import com.sleepkqq.sololeveling.proto.player.SearchPlayerBalanceTransactionsResponse
import org.babyfish.jimmer.Page
import org.mapstruct.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Suppress("unused")
@Mapper(
	componentModel = "spring",
	unmappedTargetPolicy = ReportingPolicy.IGNORE,
	collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
	nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT,
	nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
abstract class ProtoMapper {

	fun map(input: com.sleepkqq.sololeveling.proto.player.PlayerTaskStatus): PlayerTaskStatus =
		PlayerTaskStatus.valueOf(input.name)

	fun map(input: com.sleepkqq.sololeveling.proto.player.TaskRarity): TaskRarity =
		TaskRarity.valueOf(input.name)

	fun map(input: com.sleepkqq.sololeveling.proto.player.TaskTopic): TaskTopic =
		TaskTopic.valueOf(input.name)

	fun map(input: com.sleepkqq.sololeveling.proto.user.UserRole): UserRole =
		UserRole.valueOf(input.name)

	fun map(input: com.sleepkqq.sololeveling.proto.player.Assessment): Assessment =
		Assessment.valueOf(input.name)

	fun map(input: LocalDateTime): Timestamp = input.toTimestamp()

	@Mapping(target = "isActive", source = "active")
	abstract fun map(input: PlayerTaskTopicView): com.sleepkqq.sololeveling.proto.player.PlayerTaskTopicView

	@Mapping(target = "task.topicsList", source = "input.task.topics")
	abstract fun map(input: PlayerTaskView): com.sleepkqq.sololeveling.proto.player.PlayerTaskView

	fun map(balance: BigDecimal, currencyCode: CurrencyCode): Money = balance.toMoney(currencyCode)

	abstract fun map(input: UserView): com.sleepkqq.sololeveling.proto.user.UserView

	@Mapping(
		target = "balance",
		expression = "java(map(input.getBalance(), input.getCurrencyCode()))"
	)
	abstract fun map(input: PlayerBalanceView): com.sleepkqq.sololeveling.proto.player.PlayerBalanceView

	@Mapping(
		target = "balance.balance",
		expression = "java(map(targetOf_balance.getBalance(), targetOf_balance.getCurrencyCode()))"
	)
	@Mapping(target = "taskTopicsList", source = "taskTopics")
	abstract fun map(input: PlayerView): com.sleepkqq.sololeveling.proto.player.PlayerView

	@Mapping(target = "task.topics", source = "input.task.topicsList")
	abstract fun map(input: com.sleepkqq.sololeveling.proto.player.PlayerTaskInput): PlayerTaskInput

	@Mapping(target = "roles", source = "rolesList")
	abstract fun map(input: com.sleepkqq.sololeveling.proto.user.UserInput): UserInput

	@Mapping(target = "active", source = "isActive")
	abstract fun map(input: com.sleepkqq.sololeveling.proto.player.PlayerTaskTopicInput): PlayerTaskTopicInput

	@Mapping(target = "transactionsList", source = "rows")
	@Mapping(
		target = "transactionsList.amount",
		expression = "java(map(playerBalanceTransactionView.getAmount(), playerBalanceTransactionView.getCurrencyCode()))"
	)
	@Mapping(target = "pagination.totalRowCount", source = "totalRowCount")
	@Mapping(target = "pagination.totalPageCount", source = "totalPageCount")
	abstract fun map(input: Page<PlayerBalanceTransactionView>): SearchPlayerBalanceTransactionsResponse.Builder
}
