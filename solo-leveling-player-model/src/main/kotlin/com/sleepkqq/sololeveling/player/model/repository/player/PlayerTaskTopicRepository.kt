package com.sleepkqq.sololeveling.player.model.repository.player

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTaskTopic
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerTaskTopicView
import org.babyfish.jimmer.spring.repository.KRepository
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PlayerTaskTopicRepository : KRepository<PlayerTaskTopic, UUID> {

	fun findByPlayerIdAndIsActiveTrue(playerId: Long): List<PlayerTaskTopicView>
	fun findByPlayerId(playerId: Long): List<PlayerTaskTopic>
	fun upsertAll(entities: Collection<PlayerTaskTopic>) =
		saveEntities(entities, SaveMode.UPSERT)
}
