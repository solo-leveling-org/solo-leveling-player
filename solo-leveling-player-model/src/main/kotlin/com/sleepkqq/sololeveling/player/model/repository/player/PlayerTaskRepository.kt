package com.sleepkqq.sololeveling.player.model.repository.player

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTask
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerTaskView
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerTaskStatus
import com.sleepkqq.sololeveling.player.model.entity.player.playerId
import com.sleepkqq.sololeveling.player.model.entity.player.status
import com.sleepkqq.sololeveling.player.model.entity.player.taskId
import org.babyfish.jimmer.spring.repository.KRepository
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.expression.valueIn
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PlayerTaskRepository : KRepository<PlayerTask, UUID> {

	fun findByPlayerIdAndStatusIn(
		playerId: Long,
		statuses: Collection<PlayerTaskStatus>
	): List<PlayerTaskView> = sql.createQuery(PlayerTask::class) {
		where(
			table.playerId eq playerId,
			table.status valueIn statuses
		)
		select(table.fetch(PlayerTaskView::class))
	}
		.execute()

	fun countByPlayerIdAndStatusIn(
		playerId: Long,
		statuses: Collection<PlayerTaskStatus>
	): Long = sql.createQuery(PlayerTask::class) {
		where(
			table.playerId eq playerId,
			table.status valueIn statuses
		)
		selectCount()
	}
		.fetchOne()

	fun findByPlayerIdAndTaskIdsIn(playerId: Long, taskIds: Collection<UUID>): List<PlayerTask> =
		sql.createQuery(PlayerTask::class) {
			where(
				table.playerId eq playerId,
				table.taskId valueIn taskIds
			)
			select(table)
		}
			.execute()

	fun getTasksCountByPlayerId(playerId: Long): Long =
		sql.createQuery(PlayerTask::class) {
			where(table.playerId eq playerId)
			selectCount()
		}
			.fetchOne()
}
