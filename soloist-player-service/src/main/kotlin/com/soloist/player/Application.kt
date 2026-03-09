package com.soloist.player

import org.babyfish.jimmer.spring.repository.EnableJimmerRepositories
import org.babyfish.jimmer.sql.EnableDtoGeneration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@EnableDtoGeneration
@EnableJimmerRepositories("com.soloist.player.model.repository")
@ConfigurationPropertiesScan("com.soloist.player.config.properties")
@SpringBootApplication(scanBasePackages = ["com.soloist.player"])
class Application

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}
