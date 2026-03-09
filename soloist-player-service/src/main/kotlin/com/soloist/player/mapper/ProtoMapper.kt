package com.soloist.player.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.protobuf.Struct
import com.google.protobuf.Timestamp
import com.google.protobuf.Value
import com.google.type.Decimal
import com.google.type.Money
import com.soloist.jimmer.mapper.JimmerProtoMapper
import com.soloist.player.config.properties.PlayerLimitsProperties.StaminaConfig
import com.soloist.player.extenstions.toGoogleDecimal
import com.soloist.player.extenstions.toMoney
import com.soloist.player.extenstions.toTimestamp
import com.soloist.player.model.entity.gacha.dto.GachaMachineInput
import com.soloist.player.model.entity.gacha.dto.GachaMachineView
import com.soloist.player.model.entity.gear.dto.GearItemView
import com.soloist.player.model.entity.gear.sealed.GearItemAttributes
import com.soloist.player.model.entity.localization.LocalizationItem
import com.soloist.player.model.entity.player.TaskTopicItem
import com.soloist.player.model.entity.balance.dto.BalanceTransactionView
import com.soloist.player.model.entity.balance.dto.BalanceView
import com.soloist.player.model.entity.player.dto.CompleteTaskPlayerView
import com.soloist.player.model.entity.player.dto.PlayerGearItemView
import com.soloist.player.model.entity.task.dto.DailyTaskView
import com.soloist.player.model.entity.player.dto.DayStreakView
import com.soloist.player.model.entity.player.dto.LevelView
import com.soloist.player.model.entity.task.dto.PlayerTaskTopicView
import com.soloist.player.model.entity.task.dto.PlayerTaskView
import com.soloist.player.model.entity.player.dto.PlayerView
import com.soloist.player.model.entity.player.dto.StaminaView
import com.soloist.player.model.entity.player.enums.CurrencyCode
import com.soloist.player.model.entity.player.sealed.DailyTaskSpec
import com.soloist.player.model.entity.user.LeaderboardUser
import com.soloist.player.model.entity.user.UserRoleItem
import com.soloist.player.model.entity.user.UsersStats
import com.soloist.player.model.entity.user.dto.LocaleUserView
import com.soloist.player.model.entity.user.dto.UserInput
import com.soloist.player.model.entity.user.dto.UserView
import com.soloist.player.service.i18n.I18nService
import com.soloist.proto.balance.SearchBalanceTransactionsResponse
import com.soloist.proto.common.LocalizedField
import com.soloist.proto.common.ResponsePaging
import com.soloist.proto.common.ResponseQueryOptions
import com.soloist.proto.common.TaskTopic
import com.soloist.proto.common.UserRole
import com.soloist.proto.gacha.GachaMachineInput as ProtoGachaMachineInput
import com.soloist.proto.gacha.GachaMachineView as ProtoGachaMachineView
import com.soloist.proto.gacha.GearItemInput as ProtoGearItemInput
import com.soloist.proto.gacha.GearItemView as ProtoGearItemView
import com.soloist.proto.gacha.PlayerGearItemView as ProtoPlayerGearItemView
import com.soloist.proto.common.LocalizationInput as ProtoLocalizationInput
import com.soloist.proto.player.PlayerTaskTopicInput
import com.soloist.proto.task.SearchClosedTasksResponse
import com.soloist.proto.user.GetUsersLeaderboardResponse
import com.soloist.proto.user.GetUsersResponse
import com.soloist.proto.user.GetUsersStatsResponse
import com.soloist.proto.user.UserLocale
import org.babyfish.jimmer.Page
import org.babyfish.jimmer.View
import org.mapstruct.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.i18n.LocaleContextHolder
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import kotlin.math.max

@Mapper(
	componentModel = "spring",
	unmappedTargetPolicy = ReportingPolicy.IGNORE,
	collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
	nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT,
	nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
abstract class ProtoMapper : JimmerProtoMapper() {

	@Autowired
	protected lateinit var i18nService: I18nService

	fun map(input: TaskTopic): com.soloist.player.model.entity.task.enums.TaskTopic =
		com.soloist.player.model.entity.task.enums.TaskTopic.valueOf(input.name)

	fun map(input: View<UserRoleItem>): UserRole = UserRole.valueOf(input.toEntity().role().name)

	fun map(input: View<TaskTopicItem>): TaskTopic = TaskTopic.valueOf(input.toEntity().topic().name)

	fun map(input: Instant): Timestamp = input.toTimestamp()

	fun map(input: View<LocalizationItem>): String = input.toEntity()
		.let { if (LocaleContextHolder.getLocale().language == "ru") it.ru() else it.en() }

	@Mapping(target = "isActive", source = "active")
	@Mapping(target = "isDisabled", expression = "java(input.getTaskTopic().isDisabled())")
	abstract fun map(input: PlayerTaskTopicView): com.soloist.proto.player.PlayerTaskTopicView

	@Mapping(target = "task.topicsList", source = "input.task.topics")
	abstract fun map(input: PlayerTaskView): com.soloist.proto.task.PlayerTaskView

	fun map(balance: BigDecimal, currencyCode: CurrencyCode): Money = balance.toMoney(currencyCode)

	fun map(input: Number): Decimal = input.toGoogleDecimal()

	fun map(input: BigDecimal): Decimal = input.toGoogleDecimal()

	fun map(input: Decimal): BigDecimal = BigDecimal(input.value)

	@Autowired
	protected lateinit var objectMapper: ObjectMapper

	fun map(input: GearItemAttributes?): Struct {
		if (input == null) return Struct.getDefaultInstance()
		val json = objectMapper.writeValueAsString(input)
		@Suppress("UNCHECKED_CAST")
		val map = objectMapper.readValue(json, Map::class.java) as Map<String, Any?>
		val builder = Struct.newBuilder()
		map.forEach { (key, value) -> builder.putFields(key, toProtoValue(value)) }
		return builder.build()
	}

	fun map(input: Struct?): GearItemAttributes? {
		if (input == null || input.fieldsCount == 0) return null
		val map = input.fieldsMap.mapValues { fromProtoValue(it.value) }
		val json = objectMapper.writeValueAsString(map)
		return objectMapper.readValue(json, GearItemAttributes::class.java)
	}

	fun map(input: com.soloist.player.model.entity.gear.enums.GearItemType): com.soloist.proto.gacha.GearItemCategory =
		com.soloist.proto.gacha.GearItemCategory.valueOf(input.category.name)

	private fun toProtoValue(value: Any?): Value = when (value) {
		null -> Value.newBuilder().setNullValue(com.google.protobuf.NullValue.NULL_VALUE).build()
		is Number -> Value.newBuilder().setNumberValue(value.toDouble()).build()
		is String -> Value.newBuilder().setStringValue(value).build()
		is Boolean -> Value.newBuilder().setBoolValue(value).build()
		is Map<*, *> -> {
			val struct = Struct.newBuilder()
			@Suppress("UNCHECKED_CAST")
			(value as Map<String, Any?>).forEach { (k, v) -> struct.putFields(k, toProtoValue(v)) }
			Value.newBuilder().setStructValue(struct).build()
		}
		is List<*> -> {
			val list = com.google.protobuf.ListValue.newBuilder()
			value.forEach { list.addValues(toProtoValue(it)) }
			Value.newBuilder().setListValue(list).build()
		}
		else -> Value.newBuilder().setStringValue(value.toString()).build()
	}

	private fun fromProtoValue(value: Value): Any = when (value.kindCase) {
		Value.KindCase.NUMBER_VALUE -> value.numberValue
		Value.KindCase.STRING_VALUE -> value.stringValue
		Value.KindCase.BOOL_VALUE -> value.boolValue
		Value.KindCase.STRUCT_VALUE -> value.structValue.fieldsMap.mapValues { fromProtoValue(it.value) }
		Value.KindCase.LIST_VALUE -> value.listValue.valuesList.map { fromProtoValue(it) }
		else -> ""
	}

	@Mapping(target = "rolesList", source = "roles")
	@Mapping(target = "locale", expression = "java(map(input.getLocale(), input.getManualLocale()))")
	abstract fun map(input: UserView): com.soloist.proto.user.UserView

	protected fun map(locale: String, manualLocale: String?): UserLocale = UserLocale.newBuilder()
		.setTag(manualLocale ?: locale)
		.setIsManual(manualLocale != null)
		.build()

	@Mapping(
		target = "amount",
		expression = "java(map(input.getAmount(), input.getCurrencyCode()))"
	)
	abstract fun map(input: BalanceView): com.soloist.proto.balance.BalanceView

	@Mapping(
		target = "balance.amount",
		expression = "java(map(targetOf_balance.getAmount(), targetOf_balance.getCurrencyCode()))"
	)
	@Mapping(target = "taskTopicsList", source = "taskTopics")
	abstract fun map(input: CompleteTaskPlayerView): com.soloist.proto.player.CompleteTaskPlayerView

	abstract fun map(input: PlayerView): com.soloist.proto.player.PlayerView

	abstract fun map(input: com.soloist.proto.user.UserInput): UserInput

	@Mapping(target = "active", source = "isActive")
	abstract fun map(input: PlayerTaskTopicInput): com.soloist.player.model.entity.task.dto.PlayerTaskTopicInput

	@Mapping(target = "transactionsList", source = "page.rows")
	@Mapping(
		target = "transactionsList.amount",
		expression = "java(map(balanceTransactionView.getAmount(), balanceTransactionView.getCurrencyCode()))"
	)
	@Mapping(target = "options", expression = "java(map(filters, sorts))")
	@Mapping(
		target = "paging",
		expression = "java(map(page.getTotalRowCount(), page.getTotalPageCount(), currentPageSize))"
	)
	abstract fun mapTransactions(
		page: Page<BalanceTransactionView>,
		currentPage: Int,
		currentPageSize: Int,
		filters: List<LocalizedField>,
		sorts: Set<String>
	): SearchBalanceTransactionsResponse

	@Mapping(target = "tasksList", source = "page.rows")
	@Mapping(target = "options", expression = "java(map(filters, sorts))")
	@Mapping(
		target = "paging",
		expression = "java(map(page.getTotalRowCount(), page.getTotalPageCount(), currentPageSize))"
	)
	abstract fun mapTasks(
		page: Page<PlayerTaskView>,
		currentPage: Int,
		currentPageSize: Int,
		filters: List<LocalizedField>,
		sorts: Set<String> = setOf()
	): SearchClosedTasksResponse

	@Mapping(target = "filtersList", source = "filters")
	@Mapping(target = "sortsList", source = "sorts")
	abstract fun map(filters: List<LocalizedField>, sorts: Set<String>): ResponseQueryOptions

	abstract fun map(totalRowCount: Long, totalPageCount: Long, currentPageSize: Int): ResponsePaging

	@Mapping(target = "usersList", source = "page.rows")
	@Mapping(
		target = "paging",
		expression = "java(map(page.getTotalRowCount(), page.getTotalPageCount(), currentPageSize))"
	)
	abstract fun mapLeaderboardUsers(
		page: Page<LeaderboardUser>,
		currentPage: Int,
		currentPageSize: Int
	): GetUsersLeaderboardResponse

	@Mapping(target = "id", source = "input.user.id")
	@Mapping(target = "firstName", source = "input.user.firstName")
	@Mapping(target = "lastName", source = "input.user.lastName")
	@Mapping(target = "photoUrl", source = "input.user.photoUrl")
	abstract fun map(input: LeaderboardUser): com.soloist.proto.user.LeaderboardUser

	abstract fun map(input: UsersStats): GetUsersStatsResponse

	@Mapping(target = "isRegenerating", source = "input.regenerating")
	@Mapping(
		target = "nextRegenAt",
		expression = "java(map(input.getLastRegeneratedAt(), input.isRegenerating(), cfg.getRegenIntervalSeconds()))"
	)
	@Mapping(
		target = "fullRegenAt",
		expression = "java(map(input.getCurrent(), input.getLastRegeneratedAt(), input.isRegenerating(), cfg.getMax(), cfg.getRegenRate(), cfg.getRegenIntervalSeconds()))"
	)
	abstract fun map(input: StaminaView, cfg: StaminaConfig): com.soloist.proto.player.StaminaView

	@Mapping(
		target = "isExtendedToday",
		expression = "java(isExtendedToday(input.getUpdatedAt(), input.getMax()))"
	)
	abstract fun map(input: DayStreakView): com.soloist.proto.player.DayStreakView

	fun isExtendedToday(updatedAt: Instant, max: Int): Boolean {
		if (max == 0) {
			return false
		}
		val today = LocalDate.now(ZoneOffset.UTC)
		val updatedDate = updatedAt.atZone(ZoneOffset.UTC).toLocalDate()
		return updatedDate.isEqual(today)
	}

	@Mapping(target = "isCompleted", source = "completed")
	@Mapping(target = "goal", expression = "java(map(input.getSpec().goal()))")
	@Mapping(target = "title", expression = "java(map(input.getSpec()))")
	abstract fun map(input: DailyTaskView): com.soloist.proto.task.DailyTaskView

	protected fun map(spec: DailyTaskSpec): String =
		i18nService.getMessage(spec.fullLocalizationKey(), spec.localizationArgs())

	protected fun map(
		lastRegeneratedAt: Instant,
		isRegenerating: Boolean,
		regenIntervalSeconds: Int
	): Timestamp {

		if (!isRegenerating) {
			return Timestamp.getDefaultInstance()
		}

		val now = Instant.now()
		val secondsSinceLastUpdate = Duration.between(lastRegeneratedAt, now).seconds
		val secondsUntilNext = regenIntervalSeconds - (secondsSinceLastUpdate % regenIntervalSeconds)
		val nextRegenAt = now.plusSeconds(secondsUntilNext)
		return Timestamp.newBuilder()
			.setSeconds(nextRegenAt.epochSecond)
			.setNanos(nextRegenAt.nano)
			.build()
	}

	protected fun map(
		current: Int,
		lastRegeneratedAt: Instant,
		isRegenerating: Boolean,
		max: Int,
		regenRate: Int,
		regenIntervalSeconds: Int
	): Timestamp {

		if (!isRegenerating) {
			return Timestamp.getDefaultInstance()
		}

		val staminaNeeded = max - current
		if (staminaNeeded <= 0) {
			return Timestamp.getDefaultInstance()
		}

		val now = Instant.now()
		val secondsSinceLastUpdate = Duration.between(lastRegeneratedAt, now).seconds
		val intervalsNeeded = (staminaNeeded + regenRate - 1) / regenRate
		val totalSecondsNeeded = intervalsNeeded * regenIntervalSeconds
		val secondsRemaining = max(0L, totalSecondsNeeded - secondsSinceLastUpdate)

		val fullRegenAt = now.plusSeconds(secondsRemaining)

		return Timestamp.newBuilder()
			.setSeconds(fullRegenAt.epochSecond)
			.setNanos(fullRegenAt.nano)
			.build()
	}

	@Mapping(target = "usersList", source = "page.rows")
	@Mapping(
		target = "usersList.locale",
		expression = "java(mapLocale(localeUserView.getLocale(), localeUserView.getManualLocale()))"
	)
	@Mapping(
		target = "paging",
		expression = "java(map(page.getTotalRowCount(), page.getTotalPageCount(), currentPageSize))"
	)
	abstract fun mapLocaleUsers(page: Page<LocaleUserView>, currentPage: Int, currentPageSize: Int): GetUsersResponse

	fun mapLocale(locale: String, manualLocale: String?): String = manualLocale ?: locale

	abstract fun map(input: LevelView): com.soloist.proto.player.LevelView

	@Mapping(target = "category", expression = "java(map(input.getType()))")
	abstract fun map(input: GearItemView): ProtoGearItemView

	abstract fun map(input: PlayerGearItemView): ProtoPlayerGearItemView

	abstract fun map(input: GachaMachineView): ProtoGachaMachineView

	// ── Enum mappings ────────────────────────────────────────

	fun map(input: com.soloist.proto.gacha.GearItemType): com.soloist.player.model.entity.gear.enums.GearItemType =
		com.soloist.player.model.entity.gear.enums.GearItemType.valueOf(input.name)

	fun map(input: com.soloist.proto.common.Rarity): com.soloist.player.model.entity.player.enums.Rarity =
		com.soloist.player.model.entity.player.enums.Rarity.valueOf(input.name)

	fun map(input: com.soloist.proto.gacha.PlayerGearItemStatus): com.soloist.player.model.entity.player.enums.PlayerGearItemStatus =
		com.soloist.player.model.entity.player.enums.PlayerGearItemStatus.valueOf(input.name)

	// ── Localization mapping ─────────────────────────────────

	fun map(input: ProtoLocalizationInput): com.soloist.player.model.entity.gear.dto.GearItemInput.TargetOf_title =
		com.soloist.player.model.entity.gear.dto.GearItemInput.TargetOf_title().apply {
			en = input.en
			ru = input.ru
		}

	fun mapToDescription(input: ProtoLocalizationInput): com.soloist.player.model.entity.gear.dto.GearItemInput.TargetOf_description =
		com.soloist.player.model.entity.gear.dto.GearItemInput.TargetOf_description().apply {
			en = input.en
			ru = input.ru
		}

	fun mapToMachineName(input: ProtoLocalizationInput): GachaMachineInput.TargetOf_name =
		GachaMachineInput.TargetOf_name().apply {
			en = input.en
			ru = input.ru
		}

	fun mapToMachineDescription(input: ProtoLocalizationInput): GachaMachineInput.TargetOf_description =
		GachaMachineInput.TargetOf_description().apply {
			en = input.en
			ru = input.ru
		}

	// ── GearItem input mapping ───────────────────────────────

	@Mapping(target = "title", expression = "java(map(input.getTitle()))")
	@Mapping(target = "description", expression = "java(mapToDescription(input.getDescription()))")
	@Mapping(target = "attributes", expression = "java(map(input.getAttributes()))")
	abstract fun map(input: ProtoGearItemInput): com.soloist.player.model.entity.gear.dto.GearItemInput

	// ── GachaMachine input mapping ───────────────────────────

	@Mapping(target = "name", expression = "java(mapToMachineName(input.getName()))")
	@Mapping(target = "description", expression = "java(mapToMachineDescription(input.getDescription()))")
	@Mapping(target = "costAmount", expression = "java(new java.math.BigDecimal(input.getCostAmount()))")
	@Mapping(target = "costCurrencyCode", expression = "java(com.soloist.player.model.entity.player.enums.CurrencyCode.values()[input.getCostCurrencyCode()])")
	abstract fun map(input: ProtoGachaMachineInput): GachaMachineInput
}