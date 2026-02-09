package com.sleepkqq.sololeveling.player.event

import com.sleepkqq.sololeveling.player.model.entity.Immutables
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerDailyTask
import com.sleepkqq.sololeveling.player.model.entity.player.enums.Rarity
import com.sleepkqq.sololeveling.player.model.entity.player.sealed.CompleteTasks
import com.sleepkqq.sololeveling.player.model.entity.player.sealed.CompleteSpecifiedRarityTask
import com.sleepkqq.sololeveling.player.model.entity.player.sealed.DailyTaskSpec
import com.sleepkqq.sololeveling.player.model.entity.player.sealed.SpendCurrency
import com.sleepkqq.sololeveling.player.service.player.PlayerDailyTaskService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import java.math.BigDecimal

@Service
class DailyTaskProgressTracker(
	private val playerDailyTaskService: PlayerDailyTaskService
) {

	private val log = LoggerFactory.getLogger(javaClass)

	@Transactional
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	fun listen(event: DailyTaskProgressEvent) {
		val dailyTask = playerDailyTaskService.find(event.playerId, event.type)
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
				log.info("Daily task completed for player {} type {}", event.playerId, event.type)
			}

		} catch (e: Exception) {
			log.error(
				"Failed to update daily task progress for player {} type {}",
				event.playerId, event.type, e
			)
		}
	}

	private fun calculateProgressAmount(
		task: PlayerDailyTask,
		event: DailyTaskProgressEvent
	): BigDecimal = when (event) {
		is TaskCompletedEvent -> calculateTaskCompletionProgress(task.spec(), event.taskRarity)
		is CurrencySpentEvent -> calculateCurrencyProgress(task.spec(), event.amount)
	}

	private fun calculateTaskCompletionProgress(spec: DailyTaskSpec, taskRarity: Rarity): BigDecimal {
		return when (spec) {
			is CompleteTasks -> BigDecimal.ONE
			is CompleteSpecifiedRarityTask -> {
				if (spec.rarity == taskRarity) BigDecimal.ONE else BigDecimal.ZERO
			}

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
