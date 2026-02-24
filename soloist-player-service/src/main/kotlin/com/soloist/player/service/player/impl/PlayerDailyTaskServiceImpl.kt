package com.soloist.player.service.player.impl

import com.soloist.player.model.entity.Immutables
import com.soloist.player.model.entity.player.PlayerDailyTask
import com.soloist.player.model.entity.player.enums.DailyTaskType
import com.soloist.player.model.entity.player.sealed.CompleteTasks
import com.soloist.player.model.entity.player.sealed.DailyTaskSpec
import com.soloist.player.model.entity.player.sealed.SpendCurrency
import com.soloist.player.model.repository.player.PlayerDailyTaskRepository
import com.soloist.player.service.player.PlayerDailyTaskService
import org.babyfish.jimmer.View
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.*
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
	override fun replace(type: DailyTaskType): Long =
		playerDailyTaskRepository.replace(type, pickSpec(type))

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
	override fun <V : View<PlayerDailyTask>> findView(playerId: Long, viewType: KClass<V>): List<V> =
		playerDailyTaskRepository.findView(playerId, viewType.java)

	@Transactional(readOnly = true)
	override fun find(playerId: Long, type: DailyTaskType): PlayerDailyTask? =
		playerDailyTaskRepository.findNullable(playerId, type)

	@Transactional
	override fun update(task: PlayerDailyTask): PlayerDailyTask =
		playerDailyTaskRepository.save(task, SaveMode.UPDATE_ONLY)
}