package com.sleepkqq.sololeveling.player.lozalization

enum class LocalizationCode(val code: String) {
	TASKS_GENERATION_SUCCESS("tasks.generation.success"),
	TABLES_PLAYER_BALANCE_TRANSACTIONS("tables.player.balance.transactions"),
	TABLES_PLAYER_TASKS("tables.player.tasks");

	override fun toString(): String = code
}
