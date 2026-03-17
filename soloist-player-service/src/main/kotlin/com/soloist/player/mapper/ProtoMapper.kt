package com.soloist.player.mapper

import com.google.protobuf.Timestamp
import com.google.type.Decimal
import com.google.type.Money
import com.soloist.jimmer.mapper.JimmerProtoMapper
import com.soloist.player.config.properties.PlayerLimitsProperties.StaminaConfig
import com.soloist.player.extenstions.toGoogleDecimal
import com.soloist.player.extenstions.toMoney
import com.soloist.player.extenstions.toTimestamp
import com.soloist.player.model.entity.balance.dto.BalanceTransactionView
import com.soloist.player.model.entity.balance.dto.BalanceView
import com.soloist.player.model.entity.player.dto.DayStreakView
import com.soloist.player.model.entity.player.dto.PlayerView
import com.soloist.player.model.entity.player.dto.StaminaView
import com.soloist.player.model.entity.player.enums.CurrencyCode
import com.soloist.player.model.entity.task.Task
import com.soloist.player.model.entity.task.enums.TaskType
import com.soloist.player.model.entity.task.enums.ProofType
import com.soloist.player.model.entity.user.LeaderboardUser
import com.soloist.player.model.entity.user.UserRoleItem
import com.soloist.player.model.entity.user.UsersStats
import com.soloist.player.model.entity.user.dto.LocaleUserView
import com.soloist.player.model.entity.user.dto.UserInput
import com.soloist.player.model.entity.user.dto.UserView
import com.soloist.proto.admin.GetUsersResponse
import com.soloist.proto.admin.GetUsersStatsResponse
import com.soloist.proto.balance.SearchBalanceTransactionsResponse
import com.soloist.proto.common.LocalizedField
import com.soloist.proto.common.ResponsePaging
import com.soloist.proto.common.ResponseQueryOptions
import com.soloist.proto.common.UserRole
import com.soloist.proto.task.TaskView
import com.soloist.proto.user.GetUsersLeaderboardResponse
import com.soloist.proto.user.UserLocale
import com.soloist.proto.common.TaskType as ProtoTaskType
import com.soloist.proto.common.ProofType as ProtoProofType
import org.babyfish.jimmer.Page
import org.babyfish.jimmer.View
import org.mapstruct.CollectionMappingStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.NullValueCheckStrategy
import org.mapstruct.NullValueMappingStrategy
import org.mapstruct.NullValuePropertyMappingStrategy
import org.mapstruct.ReportingPolicy
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
	protected lateinit var i18nService: com.soloist.player.service.i18n.I18nService

	fun map(input: View<UserRoleItem>): UserRole = UserRole.valueOf(input.toEntity().role().name)

	fun map(input: Instant): Timestamp = input.toTimestamp()

	fun map(input: View<com.soloist.player.model.entity.localization.LocalizationItem>): String =
		input.toEntity()
			.let { if (LocaleContextHolder.getLocale().language == "ru") it.ru() else it.en() }

	abstract fun map(input: PlayerView): com.soloist.proto.player.PlayerView

	abstract fun map(input: com.soloist.proto.user.UserInput): UserInput

	fun map(balance: BigDecimal, currencyCode: CurrencyCode): Money = balance.toMoney(currencyCode)

	fun map(input: Number): Decimal = input.toGoogleDecimal()

	fun map(input: BigDecimal): Decimal = input.toGoogleDecimal()

	fun map(input: Decimal): BigDecimal = BigDecimal(input.value)

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
	abstract fun mapLocaleUsers(
		page: Page<LocaleUserView>,
		currentPage: Int,
		currentPageSize: Int
	): GetUsersResponse

	fun mapLocale(locale: String, manualLocale: String?): String = manualLocale ?: locale

	// ── Enum mappings ────────────────────────────────────────

	fun map(input: com.soloist.proto.common.CurrencyCode): CurrencyCode =
		CurrencyCode.valueOf(input.name)

	fun map(input: CurrencyCode): com.soloist.proto.common.CurrencyCode =
		com.soloist.proto.common.CurrencyCode.valueOf(input.name)

	fun map(input: com.soloist.player.model.entity.balance.enums.BalanceTransactionCause): com.soloist.proto.common.BalanceTransactionCause =
		when (input) {
			com.soloist.player.model.entity.balance.enums.BalanceTransactionCause.TASK_COMPLETION ->
				com.soloist.proto.common.BalanceTransactionCause.TASK_COMPLETION
			com.soloist.player.model.entity.balance.enums.BalanceTransactionCause.DAILY_CHECK_IN ->
				com.soloist.proto.common.BalanceTransactionCause.DAILY_CHECK_IN
			else -> com.soloist.proto.common.BalanceTransactionCause.TASK_COMPLETION
		}

	// ── Task mappings ─────────────────────────────────────

	fun mapTask(task: Task): TaskView {
		val dayInstant = task.day().atStartOfDay(ZoneOffset.UTC).toInstant()
		val builder = TaskView.newBuilder()
			.setId(task.id().toString())
			.setType(ProtoTaskType.valueOf(task.type().name))
			.setGoal(task.goal())
			.setProgress(task.progress())
			.setGemReward(task.gemReward())
			.setIsCompleted(task.completed())
			.setProofType(ProtoProofType.valueOf(task.proofType().name))
			.setDay(
				com.google.protobuf.Timestamp.newBuilder()
					.setSeconds(dayInstant.epochSecond)
					.setNanos(dayInstant.nano)
					.build()
			)
			.setCreatedAt(task.createdAt().toTimestamp())
		task.name()?.let { builder.setName(it) }
		return builder.build()
	}

	fun map(input: TaskType): ProtoTaskType = ProtoTaskType.valueOf(input.name)

	fun map(input: ProofType): ProtoProofType = ProtoProofType.valueOf(input.name)
}
