package com.sleepkqq.sololeveling.player

import com.sleepkqq.sololeveling.player.model.entity.Immutables
import com.sleepkqq.sololeveling.player.model.entity.localization.LocalizationItem
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTask
import com.sleepkqq.sololeveling.player.model.entity.player.TaskTopicItem
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerBalanceTransactionCause
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerBalanceTransactionType
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerTaskStatus
import com.sleepkqq.sololeveling.player.model.entity.player.enums.Rarity
import com.sleepkqq.sololeveling.player.model.entity.task.Task
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic
import com.sleepkqq.sololeveling.player.model.entity.user.User
import com.sleepkqq.sololeveling.player.model.entity.user.enums.UserRole
import com.sleepkqq.sololeveling.player.service.player.PlayerBalanceTransactionService
import com.sleepkqq.sololeveling.player.service.user.UserService
import org.springframework.beans.factory.annotation.Autowired
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
import java.math.BigDecimal
import java.util.UUID

@Suppress("unused")
@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class BaseTestClass {

	@Autowired
	protected lateinit var userService: UserService

	@Autowired
	protected lateinit var playerBalanceTransactionService: PlayerBalanceTransactionService

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
		@ServiceConnection(name = "redis")
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
		@ServiceConnection(name = "postgresql")
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

	protected fun createUser(id: Long, username: String = "test"): User {
		return userService.upsert(
			Immutables.createUser {
				it.setId(id)
				it.setUsername(username)
				it.setFirstName(username)
				it.setLastName(username)
				it.setPhotoUrl("$username.jpg")
				it.setLocale("en")
				it.setRoles(
					listOf(
						Immutables.createUserRoleItem { i -> i.setRole(UserRole.USER) }
					)
				)
			}
		)
	}

	protected fun createPlayerBalanceTransaction(
		balanceId: UUID,
		amount: BigDecimal = BigDecimal.TEN,
		type: PlayerBalanceTransactionType = PlayerBalanceTransactionType.IN,
		cause: PlayerBalanceTransactionCause = PlayerBalanceTransactionCause.DAILY_CHECK_IN
	) {
		playerBalanceTransactionService.insert(
			Immutables.createPlayerBalanceTransaction {
				it.setAmount(amount)
				it.setType(type)
				it.setCause(cause)
				it.setBalanceId(balanceId)
			}
		)
	}

	protected fun createLocalizationItem(ru: String, en: String): LocalizationItem {
		return Immutables.createLocalizationItem { item ->
			item.setId(UUID.randomUUID())
			item.setRu(ru)
			item.setEn(en)
		}
	}

	protected fun createTaskTopicItem(topic: TaskTopic): TaskTopicItem {
		return Immutables.createTaskTopicItem { t ->
			t.setId(UUID.randomUUID())
			t.setTopic(topic)
		}
	}

	protected fun createTask(
		id: UUID = UUID.randomUUID(),
		title: String = "Test Title",
		description: String = "Test Description",
		experience: Int = 100,
		currencyReward: Int = 50,
		rarity: Rarity = Rarity.EPIC,
		agility: Int = 5,
		strength: Int = 10,
		intelligence: Int = 3,
		topics: List<TaskTopic> = emptyList(),
		version: Int = 1
	): Task {
		return Immutables.createTask {
			it.setId(id)
			it.setTitle(createLocalizationItem(description, title))
			it.setDescription(createLocalizationItem(description, description))
			it.setExperience(experience)
			it.setCurrencyReward(currencyReward)
			it.setRarity(rarity)
			it.setAgility(agility)
			it.setStrength(strength)
			it.setIntelligence(intelligence)
			it.setTopics(topics.map { topic -> createTaskTopicItem(topic) })
			it.setVersion(version)
		}
	}

	protected fun createPlayerTask(
		task: Task,
		playerId: Long,
		status: PlayerTaskStatus = PlayerTaskStatus.COMPLETED,
		order: Int = 1
	): PlayerTask {
		return Immutables.createPlayerTask {
			it.setId(UUID.randomUUID())
			it.setTask(task)
			it.setPlayerId(playerId)
			it.setStatus(status)
			it.setOrder(order)
		}
	}
}
