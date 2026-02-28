package com.soloist.player.config.properties

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@EnableConfigurationProperties(
	JobsProperties::class,
	PlayerLimitsProperties::class,
	TasksProperties::class
)
@Configuration
class AppPropertiesConfig
