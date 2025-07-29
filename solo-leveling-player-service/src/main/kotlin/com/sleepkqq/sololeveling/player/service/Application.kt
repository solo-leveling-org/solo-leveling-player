package com.sleepkqq.sololeveling.player.service

import org.babyfish.jimmer.spring.repository.EnableJimmerRepositories
import org.babyfish.jimmer.sql.EnableDtoGeneration
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer

@EnableDtoGeneration
@EnableJimmerRepositories("com.sleepkqq.sololeveling.player.model.repository")
@SpringBootApplication(scanBasePackages = ["com.sleepkqq.sololeveling.player"])
class Application : SpringBootServletInitializer() {

	companion object {
		@JvmStatic
		fun main(args: Array<String>) {
			SpringApplication.run(Application::class.java, *args)
		}
	}
}
