package com.sleepkqq.sololeveling.player.config.properties

import com.sleepkqq.sololeveling.player.model.entity.player.enums.Rarity
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.tasks")
data class TasksProperties(
	val rarityProperties: List<RarityProperty>,
	val currencyExperienceMultiplier: Double
) {

	data class RarityProperty(
		val rarity: Rarity,
		val staminaCost: Int,
		val experience: Int
	) {

		fun calculateCurrencyReward(multiplier: Double): Int = (experience * multiplier).toInt()
	}

	private val rarityMap: Map<Rarity, RarityProperty> by lazy {
		rarityProperties.associateBy { it.rarity }
	}

	fun getProperties(rarity: Rarity): RarityProperty = rarityMap[rarity]
		?: throw IllegalArgumentException("No properties configured for rarity: $rarity")

	fun calculateCurrencyReward(rarity: Rarity): Int = getProperties(rarity)
		.calculateCurrencyReward(currencyExperienceMultiplier)

	fun getStaminaCost(rarity: Rarity): Int = getProperties(rarity).staminaCost

	fun getExperience(rarity: Rarity): Int = getProperties(rarity).experience
}
