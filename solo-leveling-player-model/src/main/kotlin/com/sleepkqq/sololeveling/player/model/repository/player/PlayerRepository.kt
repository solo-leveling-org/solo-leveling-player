package com.sleepkqq.sololeveling.player.model.repository.player

import com.sleepkqq.sololeveling.player.model.entity.player.Player
import com.sleepkqq.sololeveling.player.model.entity.player.fetchBy
import com.sleepkqq.sololeveling.player.model.entity.player.id
import org.babyfish.jimmer.spring.repository.KRepository
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.springframework.stereotype.Repository

@Repository
interface PlayerRepository : KRepository<Player, Long> {

	fun findNullable(id: Long): Player? =
		sql.createQuery(Player::class) {
			where(table.id eq id)
			select(table.fetchBy {
				allScalarFields()
				user {
					allScalarFields()
				}
				level {
					allScalarFields()
				}
				taskTopics {
					allScalarFields()
					level {
						allScalarFields()
					}
				}
			})
		}
			.fetchFirstOrNull()
}
