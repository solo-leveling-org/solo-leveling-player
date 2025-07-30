package com.sleepkqq.sololeveling.player.service

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.kafka.ConfluentKafkaContainer

@Suppress("unused")
@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class BaseTestClass {

	companion object {
		@JvmStatic
		@Container
		val kafkaContainer = ConfluentKafkaContainer("confluentinc/cp-kafka:latest")
			.withExposedPorts(9092)

		@JvmStatic
		@Container
		val redisContainer = GenericContainer("redis:7")
			.withExposedPorts(6379)

		@JvmStatic
		@Container
		val schemaRegistryContainer = GenericContainer("confluentinc/cp-schema-registry:7.2.15")
			.withExposedPorts(8081)

		@JvmStatic
		@Container
		val postgresContainer = PostgreSQLContainer<Nothing>("postgres:15")
			.apply {
				withDatabaseName("sololeveling_test")
				withUsername("test")
				withPassword("test")
				withReuse(true)
				withExposedPorts(5432)
			}

		@JvmStatic
		@DynamicPropertySource
		fun configureProperties(registry: DynamicPropertyRegistry) {
			registry.add("spring.datasource.url", postgresContainer::getJdbcUrl)
			registry.add("spring.datasource.username", postgresContainer::getUsername)
			registry.add("spring.datasource.password", postgresContainer::getPassword)

			registry.add("spring.data.redis.port", redisContainer::getFirstMappedPort)
			registry.add("spring.data.redis.host", redisContainer::getHost)

			registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers)
			registry.add("spring.kafka.properties.schema.registry.url") {
				"http://${schemaRegistryContainer.host}:${schemaRegistryContainer.firstMappedPort}"
			}
		}
	}
}
