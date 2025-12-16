package com.sleepkqq.sololeveling.player.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.player")
data class PlayerLimitsProperties(
	val limits: Limits
) {
	data class Limits(
		val free: Tier,
		val premium: Tier
	)

	data class Tier(
		val tasks: TasksConfig,
		val stamina: StaminaConfig
	)

	data class TasksConfig(
		val max: Int
	)

	data class StaminaConfig(
		val max: Int,
		val regenRate: Int,
		val regenIntervalSeconds: Int
	)

	fun getLimits(isPremium: Boolean): Tier {
		return if (isPremium) limits.premium else limits.free
	}
}
