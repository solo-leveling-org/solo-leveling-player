package com.sleepkqq.sololeveling.player.event

import com.sleepkqq.sololeveling.player.event.model.CurrencySpentEvent
import com.sleepkqq.sololeveling.player.event.model.DailyTaskProgressEvent
import com.sleepkqq.sololeveling.player.event.model.TaskCompletedEvent
import com.sleepkqq.sololeveling.player.model.entity.Immutables
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerDailyTask
import com.sleepkqq.sololeveling.player.model.entity.player.sealed.CompleteTasks
import com.sleepkqq.sololeveling.player.model.entity.player.sealed.DailyTaskSpec
import com.sleepkqq.sololeveling.player.model.entity.player.sealed.SpendCurrency
import com.sleepkqq.sololeveling.player.service.player.PlayerDailyTaskService
import com.sleepkqq.sololeveling.player.service.player.PlayerDayActivityService
import com.sleepkqq.sololeveling.player.service.player.PlayerDayStreakService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.UUID

@Service
class DailyTaskProgressTracker(
	private val playerDailyTaskService: PlayerDailyTaskService,
	private val playerDayStreakService: PlayerDayStreakService,
	private val playerDayActivityService: PlayerDayActivityService
) {

	private val log = LoggerFactory.getLogger(javaClass)

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	fun listen(event: DailyTaskProgressEvent) {
		val today = LocalDate.now(ZoneOffset.UTC)
		val playerId = event.playerId
		val dailyTask = playerDailyTaskService.find(playerId, event.type)
			?: return

		if (dailyTask.completed()) {
			return
		}

		val amount = calculateProgressAmount(dailyTask, event)
		if (amount == BigDecimal.ZERO) {
			return
		}

		try {
			val updatedDailyTask = incrementProgress(dailyTask, amount)
			playerDailyTaskService.update(updatedDailyTask)

			if (updatedDailyTask.completed()) {
				log.info("Daily task completed for player {} type {}", playerId, event.type)

				playerDayStreakService.processStreak(playerId, today)

				playerDayActivityService.insertIfAbsent(Immutables.createPlayerDayActivity {
					it.setId(UUID.randomUUID())
						.setPlayerId(playerId)
						.setDailyTaskCompleted(true)
						.setDay(today)
				})
			}

		} catch (e: Exception) {
			log.error(
				"Failed to update daily task progress for player {} type {}",
				playerId, event.type, e
			)
		}
	}

	private fun calculateProgressAmount(
		task: PlayerDailyTask,
		event: DailyTaskProgressEvent
	): BigDecimal = when (event) {
		is TaskCompletedEvent -> calculateTaskCompletionProgress(task.spec())
		is CurrencySpentEvent -> calculateCurrencyProgress(task.spec(), event.amount)
	}

	private fun calculateTaskCompletionProgress(spec: DailyTaskSpec): BigDecimal {
		return when (spec) {
			is CompleteTasks -> BigDecimal.ONE
			else -> BigDecimal.ZERO
		}
	}

	private fun calculateCurrencyProgress(spec: DailyTaskSpec, amount: BigDecimal): BigDecimal {
		return when (spec) {
			is SpendCurrency -> amount
			else -> BigDecimal.ZERO
		}
	}

	private fun incrementProgress(task: PlayerDailyTask, amount: BigDecimal): PlayerDailyTask {
		val newProgress = task.progress() + amount
		val goal = task.spec().goal()
		val isCompleted = newProgress >= goal

		return Immutables.createPlayerDailyTask(task) {
			it.setProgress(if (isCompleted) goal else newProgress)
				.setCompleted(isCompleted)
		}
	}
}
