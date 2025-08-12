package com.sleepkqq.sololeveling.player.service

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.Network
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
		val kafkaContainer = ConfluentKafkaContainer("confluentinc/cp-kafka:7.6.0")
			.apply {
				withExposedPorts(9092)
				withNetwork(Network.SHARED)
			}

		@JvmStatic
		@Container
		@ServiceConnection
		val redisContainer = GenericContainer("redis:7.2.4")
			.apply {
				withExposedPorts(6379)
				withNetwork(Network.SHARED)
			}

		@JvmStatic
		@Container
		val schemaRegistryContainer = GenericContainer("confluentinc/cp-schema-registry:7.6.0")
			.apply {
				withExposedPorts(8081)
				withNetwork(Network.SHARED)
			}

		@JvmStatic
		@Container
		@ServiceConnection
		val postgresContainer = PostgreSQLContainer<Nothing>("postgres:16.2")
			.apply {
				withDatabaseName("sololeveling_test")
				withUsername("test")
				withPassword("test")
				withNetwork(Network.SHARED)
			}

		@JvmStatic
		@DynamicPropertySource
		fun configureProperties(registry: DynamicPropertyRegistry) {
			registry.add("spring.kafka.bootstrap-servers") {
				kafkaContainer.bootstrapServers
			}
			registry.add("spring.kafka.properties.schema.registry.url") {
				"http://${schemaRegistryContainer.host}:${schemaRegistryContainer.firstMappedPort}"
			}
		}
	}
}
