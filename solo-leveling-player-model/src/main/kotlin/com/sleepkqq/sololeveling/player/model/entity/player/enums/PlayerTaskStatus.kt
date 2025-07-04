package com.sleepkqq.sololeveling.player.model.entity.player.enums

import org.babyfish.jimmer.sql.EnumItem

enum class PlayerTaskStatus {
	@EnumItem(ordinal = 0)
	PREPARING,

	@EnumItem(ordinal = 1)
	IN_PROGRESS,

	@EnumItem(ordinal = 2)
	PENDING_COMPLETION,

	@EnumItem(ordinal = 3)
	COMPLETED,

	@EnumItem(ordinal = 4)
	SKIPPED
}
