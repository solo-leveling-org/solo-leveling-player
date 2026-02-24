package com.soloist.player.event.model

import com.soloist.player.model.entity.player.enums.DailyTaskType
import com.soloist.player.model.entity.player.enums.Rarity
import java.math.BigDecimal

sealed interface DailyTaskProgressEvent {
	val playerId: Long
	val type: DailyTaskType
}

data class TaskCompletedEvent(
	override val playerId: Long,
	val taskRarity: Rarity,
	override val type: DailyTaskType = DailyTaskType.TASKS,
) : DailyTaskProgressEvent

data class CurrencySpentEvent(
	override val playerId: Long,
	val amount: BigDecimal,
	override val type: DailyTaskType = DailyTaskType.CURRENCY,
) : DailyTaskProgressEvent
