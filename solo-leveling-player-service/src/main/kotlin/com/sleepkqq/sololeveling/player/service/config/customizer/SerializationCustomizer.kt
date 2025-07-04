package com.sleepkqq.sololeveling.player.service.config.customizer

import com.sleepkqq.sololeveling.player.service.mapper.EnumOrdinalMapper
import com.sleepkqq.sololeveling.player.model.entity.task.TaskProps
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic
import com.sleepkqq.sololeveling.player.model.entity.user.UserProps
import com.sleepkqq.sololeveling.player.model.entity.user.enums.UserRole
import org.babyfish.jimmer.sql.JSqlClient
import org.babyfish.jimmer.sql.runtime.Customizer
import org.springframework.stereotype.Component

@Component
class SerializationCustomizer : Customizer {

	override fun customize(builder: JSqlClient.Builder) {
		builder.setSerializedPropObjectMapper(
			TaskProps.TOPICS,
			EnumOrdinalMapper.createMapper(TaskTopic::class.java)
		)
			.setSerializedPropObjectMapper(
				UserProps.ROLES,
				EnumOrdinalMapper.createMapper(UserRole::class.java)
			)
	}
}
