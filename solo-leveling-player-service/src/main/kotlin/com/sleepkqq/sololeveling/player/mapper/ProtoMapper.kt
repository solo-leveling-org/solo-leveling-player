package com.sleepkqq.sololeveling.player.mapper

import com.google.protobuf.Timestamp
import com.google.type.Money
import com.sleepkqq.sololeveling.player.extenstions.toMoney
import com.sleepkqq.sololeveling.player.extenstions.toTimestamp
import com.sleepkqq.sololeveling.player.model.entity.localization.LocalizationItem
import com.sleepkqq.sololeveling.player.model.entity.player.TaskTopicItem
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerBalanceTransactionView
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerBalanceView
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerTaskTopicView
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerTaskView
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerView
import com.sleepkqq.sololeveling.player.model.entity.player.enums.CurrencyCode
import com.sleepkqq.sololeveling.player.model.entity.player.enums.Rarity
import com.sleepkqq.sololeveling.player.model.entity.user.dto.UserView
import com.sleepkqq.sololeveling.proto.player.*
import com.sleepkqq.sololeveling.proto.player.PlayerTaskInput
import com.sleepkqq.sololeveling.proto.player.PlayerTaskTopicInput
import com.sleepkqq.sololeveling.proto.user.UserInput
import com.sleepkqq.sololeveling.proto.user.UserRole
import org.babyfish.jimmer.Page
import org.babyfish.jimmer.View
import org.mapstruct.*
import org.springframework.context.i18n.LocaleContextHolder
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

	fun map(input: PlayerTaskStatus): com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerTaskStatus =
		com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerTaskStatus.valueOf(input.name)

	fun map(input: TaskRarity): Rarity =
		Rarity.valueOf(input.name)

	fun map(input: TaskTopic): com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic =
		com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic.valueOf(input.name)

	fun map(input: UserRole): com.sleepkqq.sololeveling.player.model.entity.user.enums.UserRole =
		com.sleepkqq.sololeveling.player.model.entity.user.enums.UserRole.valueOf(input.name)

	fun map(input: Assessment): com.sleepkqq.sololeveling.player.model.entity.player.enums.Assessment =
		com.sleepkqq.sololeveling.player.model.entity.player.enums.Assessment.valueOf(input.name)

	fun map(input: View<TaskTopicItem>): TaskTopic = TaskTopic.valueOf(input.toEntity().topic().name)

	fun map(input: LocalDateTime): Timestamp = input.toTimestamp()

	fun map(input: View<LocalizationItem>): String = input.toEntity()
		.let { if (LocaleContextHolder.getLocale().language == "ru") it.ru() else it.en() }

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

	abstract fun map(input: PlayerTaskInput): com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerTaskInput

	abstract fun map(input: UserInput): com.sleepkqq.sololeveling.player.model.entity.user.dto.UserInput

	@Mapping(target = "active", source = "isActive")
	abstract fun map(input: PlayerTaskTopicInput): com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerTaskTopicInput

	@Mapping(target = "transactionsList", source = "page.rows")
	@Mapping(
		target = "transactionsList.amount",
		expression = "java(map(playerBalanceTransactionView.getAmount(), playerBalanceTransactionView.getCurrencyCode()))"
	)
	@Mapping(
		target = "options",
		expression = "java(map(page.getTotalRowCount(), page.getTotalPageCount(), currentPage, filters, sorts))"
	)
	abstract fun map(
		page: Page<PlayerBalanceTransactionView>,
		currentPage: Int,
		filters: List<LocalizedField>,
		sorts: Set<String>
	): SearchPlayerBalanceTransactionsResponse

	@Mapping(target = "filtersList", source = "filters")
	@Mapping(target = "sortsList", source = "sorts")
	@Mapping(target = "hasMore", expression = "java(totalPageCount - 1 != currentPage)")
	abstract fun map(
		totalRowCount: Long,
		totalPageCount: Long,
		currentPage: Int,
		filters: List<LocalizedField>,
		sorts: Set<String>
	): ResponseQueryOptions
}