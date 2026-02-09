package com.sleepkqq.sololeveling.player.service.player.impl

import com.sleepkqq.sololeveling.player.model.entity.Immutables
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerDailyTask
import com.sleepkqq.sololeveling.player.model.entity.player.enums.DailyTaskType
import com.sleepkqq.sololeveling.player.model.entity.player.sealed.CompleteTasks
import com.sleepkqq.sololeveling.player.model.entity.player.sealed.DailyTaskSpec
import com.sleepkqq.sololeveling.player.model.entity.player.sealed.SpendCurrency
import com.sleepkqq.sololeveling.player.model.repository.player.PlayerDailyTaskRepository
import com.sleepkqq.sololeveling.player.service.player.PlayerDailyTaskService
import org.babyfish.jimmer.View
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.UUID
import kotlin.reflect.KClass

@Service
class PlayerDailyTaskServiceImpl(
	private val playerDailyTaskRepository: PlayerDailyTaskRepository
) : PlayerDailyTaskService {

	@Transactional
	override fun insertAll(tasks: Collection<PlayerDailyTask>) {
		playerDailyTaskRepository.saveEntities(tasks, SaveMode.INSERT_ONLY)
	}

	@Transactional
	override fun updateAll(tasks: Collection<PlayerDailyTask>) {
		playerDailyTaskRepository.saveEntities(tasks, SaveMode.UPDATE_ONLY)
	}

	override fun replace(task: PlayerDailyTask): PlayerDailyTask =
		Immutables.createPlayerDailyTask(task) {
			it.setSpec(pickSpec(task.type()))
				.setProgress(BigDecimal.ZERO)
				.setCompleted(false)
		}

	override fun initialize(playerId: Long, type: DailyTaskType): PlayerDailyTask =
		Immutables.createPlayerDailyTask {
			it.setId(UUID.randomUUID())
				.setPlayerId(playerId)
				.setSpec(pickSpec(type))
				.setType(type)
				.setProgress(BigDecimal.ZERO)
				.setCompleted(false)
		}

	private fun pickSpec(type: DailyTaskType): DailyTaskSpec = when (type) {
		DailyTaskType.TASKS -> CompleteTasks()
		DailyTaskType.CURRENCY -> SpendCurrency()
	}

	@Transactional(readOnly = true)
	override fun findPlayersToInit(type: DailyTaskType): List<Long> =
		playerDailyTaskRepository.findPlayersToInit(type)

	@Transactional(readOnly = true)
	override fun <V : View<PlayerDailyTask>> findView(viewType: KClass<V>): List<V> =
		playerDailyTaskRepository.findView(viewType.java)
}