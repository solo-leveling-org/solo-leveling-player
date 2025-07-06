package com.sleepkqq.sololeveling.player.service

import org.babyfish.jimmer.spring.repository.EnableJimmerRepositories
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@EnableJimmerRepositories("com.sleepkqq.sololeveling.player.model.repository")
@SpringBootApplication(scanBasePackages = ["com.sleepkqq.sololeveling.player"])
class Application {

	companion object {
		@JvmStatic
		fun main(args: Array<String>) {
			SpringApplication.run(Application::class.java, *args)
		}
	}
}
