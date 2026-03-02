package com.soloist.player.service.player.impl

import com.soloist.player.model.entity.Immutables
import com.soloist.player.model.entity.task.DailyTask
import com.soloist.player.model.entity.task.enums.DailyTaskType
import com.soloist.player.model.entity.player.sealed.CompleteTasks
import com.soloist.player.model.entity.player.sealed.DailyTaskSpec
import com.soloist.player.model.entity.player.sealed.SpendCurrency
import com.soloist.player.model.repository.task.DailyTaskRepository
import com.soloist.player.service.player.DailyTaskService
import org.babyfish.jimmer.View
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.*
import kotlin.reflect.KClass

@Service
class DailyTaskServiceImpl(
	private val dailyTaskRepository: DailyTaskRepository
) : DailyTaskService {

	@Transactional
	override fun insertAll(tasks: Collection<DailyTask>) {
		dailyTaskRepository.saveEntities(tasks, SaveMode.INSERT_ONLY)
	}

	@Transactional
	override fun replace(type: DailyTaskType): Long =
		dailyTaskRepository.replace(type, pickSpec(type))

	override fun initialize(playerId: Long, type: DailyTaskType): DailyTask =
		Immutables.createDailyTask {
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
		dailyTaskRepository.findPlayersToInit(type)

	@Transactional(readOnly = true)
	override fun <V : View<DailyTask>> findView(playerId: Long, viewType: KClass<V>): List<V> =
		dailyTaskRepository.findView(playerId, viewType.java)

	@Transactional(readOnly = true)
	override fun find(playerId: Long, type: DailyTaskType): DailyTask? =
		dailyTaskRepository.findNullable(playerId, type)

	@Transactional
	override fun update(task: DailyTask): DailyTask =
		dailyTaskRepository.save(task, SaveMode.UPDATE_ONLY)
}