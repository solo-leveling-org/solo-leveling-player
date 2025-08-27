package com.sleepkqq.sololeveling.player.service.config.customizer

import com.sleepkqq.sololeveling.player.model.entity.task.TaskProps
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic
import com.sleepkqq.sololeveling.player.model.entity.user.UserProps
import com.sleepkqq.sololeveling.player.model.entity.user.enums.UserRole
import com.sleepkqq.sololeveling.player.service.mapper.EnumOrdinalMapper
import org.babyfish.jimmer.sql.JSqlClient.Builder
import org.babyfish.jimmer.sql.runtime.Customizer
import org.springframework.stereotype.Component

@Suppress("unused")
@Component
class SerializationCustomizer : Customizer {

	override fun customize(builder: Builder) {
		builder.apply {
			setSerializedPropObjectMapper(
				TaskProps.TOPICS,
				EnumOrdinalMapper.createMapper(TaskTopic::class.java)
			)
			setSerializedPropObjectMapper(
				UserProps.ROLES,
				EnumOrdinalMapper.createMapper(UserRole::class.java)
			)
		}
	}
}
