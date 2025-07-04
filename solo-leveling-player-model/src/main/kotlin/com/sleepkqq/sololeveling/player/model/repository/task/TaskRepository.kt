package com.sleepkqq.sololeveling.player.model.repository.task

import com.sleepkqq.sololeveling.player.model.entity.task.Task
import com.sleepkqq.sololeveling.player.model.entity.task.id
import com.sleepkqq.sololeveling.player.model.entity.task.version
import org.babyfish.jimmer.spring.repository.KRepository
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
interface TaskRepository : KRepository<Task, UUID> {

	@Transactional
	fun findVersionById(id: UUID): Int? =
		sql.createQuery(Task::class) {
			where(table.id eq id)
			select(table.version)
		}
			.fetchFirstOrNull()
}
