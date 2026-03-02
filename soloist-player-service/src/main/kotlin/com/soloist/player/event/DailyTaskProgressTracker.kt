package com.soloist.player.event

import com.soloist.player.event.model.CurrencySpentEvent
import com.soloist.player.event.model.DailyTaskProgressEvent
import com.soloist.player.event.model.TaskCompletedEvent
import com.soloist.player.model.entity.Immutables
import com.soloist.player.model.entity.task.DailyTask
import com.soloist.player.model.entity.player.sealed.CompleteTasks
import com.soloist.player.model.entity.player.sealed.DailyTaskSpec
import com.soloist.player.model.entity.player.sealed.SpendCurrency
import com.soloist.player.service.player.DailyTaskService
import com.soloist.player.service.player.DayActivityService
import com.soloist.player.service.player.DayStreakService
import org.babyfish.jimmer.sql.exception.SaveException
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
	private val dailyTaskService: DailyTaskService,
	private val dayStreakService: DayStreakService,
	private val dayActivityService: DayActivityService
) {

	private val log = LoggerFactory.getLogger(javaClass)

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	fun listen(event: DailyTaskProgressEvent) {
		val today = LocalDate.now(ZoneOffset.UTC)
		val playerId = event.playerId
		val dailyTask = dailyTaskService.find(playerId, event.type)
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
			dailyTaskService.update(updatedDailyTask)

			if (updatedDailyTask.completed()) {
				log.info("Daily task completed for player {} type {}", playerId, event.type)

				val activityCreated = try {
					dayActivityService.insertIfAbsent(Immutables.createDayActivity {
						it.setId(UUID.randomUUID())
							.setPlayerId(playerId)
							.setDailyTaskCompleted(true)
							.setDay(today)
					})
					true

				} catch (e: SaveException.NotUnique) {
					log.debug("Daily task activity already exists for player {}", playerId, e)
					false
				}

				if (activityCreated) {
					dayStreakService.processStreak(playerId, today)
				}
			}

		} catch (e: Exception) {
			log.error(
				"Failed to update daily task progress for player {} type {}",
				playerId, event.type, e
			)
		}
	}

	private fun calculateProgressAmount(
		task: DailyTask,
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

	private fun incrementProgress(task: DailyTask, amount: BigDecimal): DailyTask {
		val newProgress = task.progress() + amount
		val goal = task.spec().goal()
		val isCompleted = newProgress >= goal

		return Immutables.createDailyTask(task) {
			it.setProgress(if (isCompleted) goal else newProgress)
				.setCompleted(isCompleted)
		}
	}
}
