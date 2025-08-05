package com.sleepkqq.sololeveling.player.service

import com.sleepkqq.sololeveling.player.model.entity.task.Task
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskRarity
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic
import com.sleepkqq.sololeveling.player.service.service.task.TaskService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime
import java.util.UUID

class ApplicationTest : BaseTestClass() {

	@Autowired
	private lateinit var taskService: TaskService

	@Test
	fun `insert should save task to database`() {
		// Arrange
		val now = LocalDateTime.now()
		val initialTask = Task {
			id = UUID.randomUUID()
			this.title = "Test Task Title"
			this.description = "Test Task Description"
			this.experience = 100
			this.currencyReward = 50
			this.rarity = TaskRarity.EPIC
			this.agility = 5
			this.strength = 10
			this.intelligence = 3
			this.topics = listOf(TaskTopic.PRODUCTIVITY, TaskTopic.SOCIAL_SKILLS)
			createdAt = now
			updatedAt = now
		}

		// Act
		val savedTask = taskService.insert(initialTask)

		val newAgilityValue = 7
		taskService.update(Task {
			id = savedTask.id
			agility = newAgilityValue
			version = savedTask.version
		})

		val updatedTask = taskService.get(initialTask.id)

		// Assert
		assertThat(updatedTask.version).isEqualTo(savedTask.version + 1)
		assertThat(updatedTask.agility).isEqualTo(newAgilityValue)
		assertThat(updatedTask.createdAt).isNotNull()
	}
}
