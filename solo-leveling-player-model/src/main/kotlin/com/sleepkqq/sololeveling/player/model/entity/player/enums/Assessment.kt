package com.sleepkqq.sololeveling.player.model.entity.player.enums

import org.babyfish.jimmer.sql.EnumItem

enum class Assessment {
	@EnumItem(ordinal = 0)
	E,

	@EnumItem(ordinal = 1)
	D,

	@EnumItem(ordinal = 2)
	C,

	@EnumItem(ordinal = 3)
	B,

	@EnumItem(ordinal = 4)
	A,

	@EnumItem(ordinal = 5)
	S
}
