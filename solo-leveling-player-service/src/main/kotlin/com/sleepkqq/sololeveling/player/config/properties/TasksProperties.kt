package com.sleepkqq.sololeveling.player.config.properties

import com.sleepkqq.sololeveling.player.model.entity.player.enums.Rarity
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.tasks")
data class TasksProperties(
	val skipStaminaCost: Int,
	val currencyExperienceMultiplier: Double,
	val rarities: List<RarityConfig>
) {

	data class RarityConfig(
		val name: Rarity,
		val stamina: Int,
		val experience: Int
	)

	private val rarityMap: Map<Rarity, RarityConfig> by lazy {
		rarities.associateBy { it.name }
	}

	fun getSkipCost(): Int = skipStaminaCost

	fun getCompleteCost(rarity: Rarity): Int = rarityMap[rarity]?.stamina
		?: throw IllegalArgumentException("No config for rarity: $rarity")

	fun getExperience(rarity: Rarity): Int = rarityMap[rarity]?.experience
		?: throw IllegalArgumentException("No config for rarity: $rarity")

	fun calculateCurrencyReward(rarity: Rarity): Int =
		(getExperience(rarity) * currencyExperienceMultiplier).toInt()
}
