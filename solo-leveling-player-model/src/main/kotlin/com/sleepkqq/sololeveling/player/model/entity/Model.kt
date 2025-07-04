package com.sleepkqq.sololeveling.player.model.entity

import org.babyfish.jimmer.sql.MappedSuperclass
import org.babyfish.jimmer.sql.Version
import java.time.LocalDateTime

@MappedSuperclass
interface Model {

	val createdAt: LocalDateTime

	val updatedAt: LocalDateTime

	@Version
	val version: Int
}
