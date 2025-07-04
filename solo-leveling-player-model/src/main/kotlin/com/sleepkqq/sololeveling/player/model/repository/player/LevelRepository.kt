package com.sleepkqq.sololeveling.player.model.repository.player

import com.sleepkqq.sololeveling.player.model.entity.player.Level
import com.sleepkqq.sololeveling.player.model.entity.player.playerId
import com.sleepkqq.sololeveling.player.model.entity.player.playerTaskTopicId
import org.babyfish.jimmer.spring.repository.KRepository
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
interface LevelRepository : KRepository<Level, UUID> {

	@Transactional
	fun findByPlayerId(playerId: Long): Level? =
		sql.createQuery(Level::class) {
			where(table.playerId eq playerId)
			select(table)
		}
			.fetchFirstOrNull()

	@Transactional
	fun findByPlayerTaskTopicId(topicId: UUID): Level? =
		sql.createQuery(Level::class) {
			where(table.playerTaskTopicId eq topicId)
			select(table)
		}
			.fetchFirstOrNull()
}
