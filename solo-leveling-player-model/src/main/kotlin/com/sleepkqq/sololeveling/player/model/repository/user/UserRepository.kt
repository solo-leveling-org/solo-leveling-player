package com.sleepkqq.sololeveling.player.model.repository.user

import com.sleepkqq.sololeveling.player.model.entity.user.User
import com.sleepkqq.sololeveling.player.model.entity.user.id
import com.sleepkqq.sololeveling.player.model.entity.user.version
import org.babyfish.jimmer.spring.repository.KRepository
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : KRepository<User, Long> {

	fun findVersionById(id: Long): Int? =
		sql.createQuery(User::class) {
			where(table.id eq id)
			select(table.version)
		}
			.fetchFirstOrNull()
}
