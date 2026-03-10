package com.soloist.player.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.inventory")
data class InventoryProperties(
	val defaultGearItemCapacity: Int,
	val defaultConsumableCapacity: Int
)
