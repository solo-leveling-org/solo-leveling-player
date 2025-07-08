package com.sleepkqq.sololeveling.player.service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

@ExtendWith(TestContainersInitializer::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = [TestContainersInitializer::class])
@ActiveProfiles("test")
@SpringBootTest
class ApplicationTest {

	@Test
	fun contextLoads() {

	}
}
