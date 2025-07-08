package com.sleepkqq.sololeveling.player.service

import com.sleepkqq.sololeveling.player.model.entity.task.Task
import com.sleepkqq.sololeveling.player.service.service.task.TaskService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

class ApplicationTest : BaseTestClass() {

	@Autowired
	lateinit var taskService: TaskService

	@Test
	fun contextLoads() {
		val taskId = UUID.randomUUID()
		taskService.insert(Task { id = taskId })

		val inserted = taskService.find(taskId)
		assertThat(inserted).isNotNull()
	}
}
