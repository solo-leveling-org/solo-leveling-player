package com.sleepkqq.sololeveling.player.service.config.customizer

import com.sleepkqq.sololeveling.player.model.entity.task.Task
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic
import com.sleepkqq.sololeveling.player.model.entity.user.User
import com.sleepkqq.sololeveling.player.model.entity.user.enums.UserRole
import com.sleepkqq.sololeveling.player.service.mapper.EnumOrdinalMapper
import org.babyfish.jimmer.sql.kt.cfg.KCustomizer
import org.babyfish.jimmer.sql.kt.cfg.KSqlClientDsl
import org.springframework.stereotype.Component

@Component
class SerializationCustomizer : KCustomizer {

	override fun customize(dsl: KSqlClientDsl) {
		dsl.apply {
			setSerializedPropObjectMapper(
				Task::topics,
				EnumOrdinalMapper.createMapper(TaskTopic::class.java)
			)
			setSerializedPropObjectMapper(
				User::roles,
				EnumOrdinalMapper.createMapper(UserRole::class.java)
			)
		}
	}
}
