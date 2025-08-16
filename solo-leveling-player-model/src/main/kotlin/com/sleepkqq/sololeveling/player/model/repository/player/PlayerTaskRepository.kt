package com.sleepkqq.sololeveling.player.model.repository.player

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTask
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerTaskStatus
import com.sleepkqq.sololeveling.player.model.entity.player.playerId
import com.sleepkqq.sololeveling.player.model.entity.player.status
import org.babyfish.jimmer.View
import org.babyfish.jimmer.spring.repository.KRepository
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.expression.valueIn
import org.springframework.stereotype.Repository
import java.util.UUID
import kotlin.reflect.KClass

@Repository
interface PlayerTaskRepository : KRepository<PlayerTask, UUID> {

	fun <V : View<PlayerTask>> findByPlayerIdAndStatusIn(
		playerId: Long,
		statuses: Collection<PlayerTaskStatus>,
		viewType: KClass<V>
	): List<V> = sql.createQuery(PlayerTask::class) {
		where(
			table.playerId eq playerId,
			table.status valueIn statuses
		)
		select(table.fetch(viewType))
	}
		.execute()

	fun countByPlayerIdAndStatusIn(playerId: Long, statuses: Collection<PlayerTaskStatus>): Long =
		sql.createQuery(PlayerTask::class) {
			where(
				table.playerId eq playerId,
				table.status valueIn statuses
			)
			selectCount()
		}
			.fetchOne()

	fun findByPlayerIdAndTaskIdIn(playerId: Long, taskIds: Collection<UUID>): List<PlayerTask>

	fun <V : View<PlayerTask>> findByStatus(status: PlayerTaskStatus, viewType: KClass<V>): List<V> =
		sql.createQuery(PlayerTask::class) {
			where(table.status eq status)
			select(table.fetch(viewType))
		}
			.execute()
}
