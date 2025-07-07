package com.sleepkqq.sololeveling.player.model.repository.player

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTask
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerTaskStatus
import com.sleepkqq.sololeveling.player.model.entity.player.fetchBy
import com.sleepkqq.sololeveling.player.model.entity.player.id
import com.sleepkqq.sololeveling.player.model.entity.player.playerId
import com.sleepkqq.sololeveling.player.model.entity.player.status
import com.sleepkqq.sololeveling.player.model.entity.player.taskId
import com.sleepkqq.sololeveling.player.model.entity.player.updatedAt
import com.sleepkqq.sololeveling.player.model.entity.player.version
import org.babyfish.jimmer.spring.repository.KRepository
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.expression.plus
import org.babyfish.jimmer.sql.kt.ast.expression.valueIn
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface PlayerTaskRepository : KRepository<PlayerTask, UUID> {

	fun findByPlayerIdAndStatusIn(
		playerId: Long,
		statuses: Collection<PlayerTaskStatus>
	): List<PlayerTask> = sql.createQuery(PlayerTask::class) {
		where(
			table.playerId eq playerId,
			table.status valueIn statuses
		)
		select(
			table.fetchBy {
				allScalarFields()
				task {
					allScalarFields()
				}
			}
		)
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

	fun findIdByPlayerIdAndTasksIdIn(playerId: Long, tasksId: Collection<UUID>): List<UUID> =
		sql.createQuery(PlayerTask::class) {
			where(
				table.playerId eq playerId,
				table.taskId valueIn tasksId
			)
			select(table.id)
		}
			.execute()

	fun setStatus(ids: Collection<UUID>, status: PlayerTaskStatus, now: LocalDateTime) =
		sql.createUpdate(PlayerTask::class) {
			where(table.id valueIn ids)
			set(table.status, status)
			set(table.version, table.version.plus(1))
			set(table.updatedAt, now)
		}
			.execute()
}
