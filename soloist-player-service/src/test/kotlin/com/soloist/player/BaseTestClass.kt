package com.soloist.player

import com.soloist.player.model.entity.Immutables
import com.soloist.player.model.entity.user.User
import com.soloist.player.model.entity.user.enums.UserRole
import com.soloist.player.service.user.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.Network
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.postgresql.PostgreSQLContainer

@Suppress("unused")
@Transactional
@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class BaseTestClass {

	@Autowired
	protected lateinit var userService: UserService

	companion object {
		private val network = Network.newNetwork()

		private val postgresContainer = PostgreSQLContainer("pgvector/pgvector:pg17")
			.apply {
				withDatabaseName("soloist_test")
				withUsername("test")
				withPassword("test")
				withNetwork(network)
				withNetworkAliases("postgres")
			}

		private val redisContainer = GenericContainer("redis:7.2.4")
			.apply {
				withExposedPorts(6379)
				withNetwork(network)
				withNetworkAliases("redis")
			}

		init {
			postgresContainer.start()
			redisContainer.start()
		}

		@JvmStatic
		@DynamicPropertySource
		fun configureProperties(registry: DynamicPropertyRegistry) {
			registry.add("spring.datasource.url", postgresContainer::getJdbcUrl)
			registry.add("spring.datasource.username", postgresContainer::getUsername)
			registry.add("spring.datasource.password", postgresContainer::getPassword)

			registry.add("spring.data.redis.host", redisContainer::getHost)
			registry.add("spring.data.redis.port") {
				redisContainer.getMappedPort(6379).toString()
			}
		}
	}

	protected fun createUser(id: Long, username: String = "test"): User =
		userService.upsert(
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
