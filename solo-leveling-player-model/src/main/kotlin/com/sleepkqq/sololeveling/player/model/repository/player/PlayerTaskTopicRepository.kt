package com.sleepkqq.sololeveling.player.model.repository.player

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTaskTopic
import org.babyfish.jimmer.spring.repository.KRepository
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PlayerTaskTopicRepository : KRepository<PlayerTaskTopic, UUID> {

	fun findByPlayerIdAndIsActiveTrue(playerId: Long): List<PlayerTaskTopic>
	fun findByPlayerId(playerId: Long): List<PlayerTaskTopic>
	fun upsertAll(entities: Collection<PlayerTaskTopic>) =
		saveEntities(entities, SaveMode.UPSERT)
}
