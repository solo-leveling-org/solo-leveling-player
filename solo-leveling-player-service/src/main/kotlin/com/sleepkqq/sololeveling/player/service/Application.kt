package com.sleepkqq.sololeveling.player.service

import org.babyfish.jimmer.spring.repository.EnableJimmerRepositories
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@EnableJimmerRepositories(value = ["com.sleepkqq.sololeveling.player.model.repository"])
@SpringBootApplication(scanBasePackages = ["com.sleepkqq.sololeveling.player"])
class Application

fun main(args: Array<String>) {
	SpringApplication.run(Application::class.java, *args)
}
