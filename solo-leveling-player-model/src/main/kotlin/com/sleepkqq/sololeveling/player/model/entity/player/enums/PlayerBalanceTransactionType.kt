package com.sleepkqq.sololeveling.player.model.entity.player.enums

import org.babyfish.jimmer.sql.EnumItem

enum class PlayerBalanceTransactionType {
	@EnumItem(ordinal = 0)
	IN,

	@EnumItem(ordinal = 1)
	OUT
}
