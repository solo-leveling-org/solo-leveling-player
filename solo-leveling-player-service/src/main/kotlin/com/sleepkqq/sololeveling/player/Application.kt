package com.sleepkqq.sololeveling.player

import org.babyfish.jimmer.spring.repository.EnableJimmerRepositories
import org.babyfish.jimmer.sql.EnableDtoGeneration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@EnableDtoGeneration
@EnableJimmerRepositories("com.sleepkqq.sololeveling.player.model.repository")
@SpringBootApplication(scanBasePackages = ["com.sleepkqq.sololeveling.player"])
class Application

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}
