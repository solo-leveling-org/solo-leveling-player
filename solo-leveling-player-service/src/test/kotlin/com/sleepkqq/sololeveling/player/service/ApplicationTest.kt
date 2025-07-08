package com.sleepkqq.sololeveling.player.service

import com.sleepkqq.sololeveling.player.model.entity.task.Task
import com.sleepkqq.sololeveling.player.model.repository.task.TaskRepository
import com.sleepkqq.sololeveling.player.service.exception.ModelNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import java.util.UUID

@ExtendWith(TestContainersInitializer::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = [TestContainersInitializer::class])
@ActiveProfiles("test")
@SpringBootTest
class ApplicationTest @Autowired constructor(
	val taskRepository: TaskRepository
) {

	@Test
	fun contextLoads() {
		val taskId = UUID.randomUUID()
		taskRepository.save(Task {
			id = taskId
		})

		val task = taskRepository.findNullable(taskId)
			?: throw ModelNotFoundException(Task::class, taskId)

		assertThat(task.id).isEqualTo(taskId)
	}
}
