package com.sleepkqq.sololeveling.player.mapper

import com.google.protobuf.Timestamp
import com.google.type.Money
import com.sleepkqq.sololeveling.jimmer.mapper.JimmerProtoMapper
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
import com.sleepkqq.sololeveling.player.model.entity.user.LeaderboardUser
import com.sleepkqq.sololeveling.player.model.entity.user.dto.UserView
import com.sleepkqq.sololeveling.proto.player.*
import com.sleepkqq.sololeveling.proto.player.PlayerTaskTopicInput
import com.sleepkqq.sololeveling.proto.user.GetUsersLeaderboardResponse
import com.sleepkqq.sololeveling.proto.user.UserInput
import org.babyfish.jimmer.Page
import org.babyfish.jimmer.View
import org.mapstruct.*
import org.springframework.context.i18n.LocaleContextHolder
import java.math.BigDecimal
import java.time.Instant

@Mapper(
	componentModel = "spring",
	unmappedTargetPolicy = ReportingPolicy.IGNORE,
	collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
	nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT,
	nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
abstract class ProtoMapper : JimmerProtoMapper() {

	fun map(input: TaskTopic): com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic =
		com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic.valueOf(input.name)

	fun map(input: View<TaskTopicItem>): TaskTopic = TaskTopic.valueOf(input.toEntity().topic().name)

	fun map(input: Instant): Timestamp = input.toTimestamp()

	fun map(input: View<LocalizationItem>): String = input.toEntity()
		.let { if (LocaleContextHolder.getLocale().language == "ru") it.ru() else it.en() }

	@Mapping(target = "isActive", source = "active")
	@Mapping(target = "isDisabled", expression = "java(input.getTaskTopic().isDisabled())")
	abstract fun map(input: PlayerTaskTopicView): com.sleepkqq.sololeveling.proto.player.PlayerTaskTopicView

	@Mapping(target = "task.topicsList", source = "input.task.topics")
	abstract fun map(input: PlayerTaskView): com.sleepkqq.sololeveling.proto.player.PlayerTaskView

	fun map(balance: BigDecimal, currencyCode: CurrencyCode): Money = balance.toMoney(currencyCode)

	fun map(input: Number): Money = input.toMoney()

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

	abstract fun map(input: UserInput): com.sleepkqq.sololeveling.player.model.entity.user.dto.UserInput

	@Mapping(target = "active", source = "isActive")
	abstract fun map(input: PlayerTaskTopicInput): com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerTaskTopicInput

	@Mapping(target = "transactionsList", source = "page.rows")
	@Mapping(
		target = "transactionsList.amount",
		expression = "java(map(playerBalanceTransactionView.getAmount(), playerBalanceTransactionView.getCurrencyCode()))"
	)
	@Mapping(target = "options", expression = "java(map(filters, sorts))")
	@Mapping(
		target = "paging",
		expression = "java(map(page.getTotalRowCount(), page.getTotalPageCount(), currentPage))"
	)
	abstract fun mapTransactions(
		page: Page<PlayerBalanceTransactionView>,
		currentPage: Int,
		filters: List<LocalizedField>,
		sorts: Set<String>
	): SearchPlayerBalanceTransactionsResponse

	@Mapping(target = "tasksList", source = "page.rows")
	@Mapping(target = "options", expression = "java(map(filters, sorts))")
	@Mapping(
		target = "paging",
		expression = "java(map(page.getTotalRowCount(), page.getTotalPageCount(), currentPage))"
	)
	abstract fun mapTasks(
		page: Page<PlayerTaskView>,
		currentPage: Int,
		filters: List<LocalizedField>,
		sorts: Set<String> = setOf()
	): SearchPlayerTasksResponse

	@Mapping(target = "filtersList", source = "filters")
	@Mapping(target = "sortsList", source = "sorts")
	abstract fun map(filters: List<LocalizedField>, sorts: Set<String>): ResponseQueryOptions

	@Mapping(target = "hasMore", expression = "java(totalPageCount - 1 != currentPage)")
	abstract fun map(totalRowCount: Long, totalPageCount: Long, currentPage: Int): ResponsePaging

	@Mapping(target = "usersList", source = "page.rows")
	@Mapping(
		target = "paging",
		expression = "java(map(page.getTotalRowCount(), page.getTotalPageCount(), currentPage))"
	)
	abstract fun map(page: Page<LeaderboardUser>, currentPage: Int): GetUsersLeaderboardResponse

	@Mapping(target = "firstName", source = "input.user.firstName")
	@Mapping(target = "lastName", source = "input.user.lastName")
	@Mapping(target = "photoUrl", source = "input.user.photoUrl")
	abstract fun map(input: LeaderboardUser): com.sleepkqq.sololeveling.proto.user.LeaderboardUser
}