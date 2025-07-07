package com.sleepkqq.sololeveling.player.model.repository.task

import com.sleepkqq.sololeveling.player.model.entity.task.Task
import com.sleepkqq.sololeveling.player.model.entity.task.description
import org.babyfish.jimmer.spring.repository.KRepository
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.babyfish.jimmer.sql.ast.mutation.UnloadedVersionBehavior
import org.babyfish.jimmer.sql.kt.ast.expression.sql
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface TaskRepository : KRepository<Task, UUID> {

	fun updateTasks(tasks: Collection<Task>) =
		sql.saveEntities(tasks, SaveMode.UPDATE_ONLY) {
			setOptimisticLock(Task::class, UnloadedVersionBehavior.INCREASE) {
				sql("coalesce(length(%e), 0) <= length(%e)") {
					expression(table.description)
					expression(newNullable(Task::description))
				}
			}
		}
}
