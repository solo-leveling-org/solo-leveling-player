package com.sleepkqq.sololeveling.player.service

import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.kafka.ConfluentKafkaContainer
import java.time.Duration


class TestContainersInitializer :
	ApplicationContextInitializer<ConfigurableApplicationContext>, AfterAllCallback {

	override fun initialize(applicationContext: ConfigurableApplicationContext) {
		postgreSQLContainer.start()
		kafkaContainer.start()

		TestPropertyValues.of(
			"spring.datasource.url=${postgreSQLContainer.getJdbcUrl()}",
			"spring.datasource.username=${postgreSQLContainer.username}",
			"spring.datasource.password=${postgreSQLContainer.password}",
			"spring.kafka.bootstrap-servers=${kafkaContainer.bootstrapServers}",
			"spring.liquibase.url=${postgreSQLContainer.getJdbcUrl()}",
			"spring.liquibase.user=${postgreSQLContainer.username}",
			"spring.liquibase.password=${postgreSQLContainer.password}"
		).applyTo(applicationContext.environment)
	}

	@Throws(Exception::class)
	override fun afterAll(context: ExtensionContext?) {
		postgreSQLContainer.close()
		kafkaContainer.close()
	}

	companion object {
		private val postgreSQLContainer = PostgreSQLContainer("postgres:latest")
			.withDatabaseName("test")
			.withUsername("postgres")
			.withPassword("postgres")
			.withExposedPorts(5432)
			.waitingFor(Wait.forListeningPort())
			.withStartupTimeout(Duration.ofSeconds(30))

		private val kafkaContainer = ConfluentKafkaContainer("confluentinc/cp-kafka:latest")
	}
}