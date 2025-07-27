package com.sleepkqq.sololeveling.player.service

import com.sleepkqq.sololeveling.player.model.entity.task.Task
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskRarity
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic
import com.sleepkqq.sololeveling.player.service.service.task.TaskService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal
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
			this.currencyReward = BigDecimal("50.75")
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
		val fetchedTask = taskService.get(savedTask.id)

		// Assert
		assertThat(savedTask.id).isEqualTo(fetchedTask.id)
		assertThat(savedTask.title).isEqualTo(fetchedTask.title)
		assertThat(savedTask.description).isEqualTo(fetchedTask.description)
		assertThat(savedTask.experience).isEqualTo(fetchedTask.experience)
		assertThat(savedTask.currencyReward).isEqualByComparingTo(fetchedTask.currencyReward)
		assertThat(savedTask.rarity).isEqualTo(fetchedTask.rarity)
		assertThat(savedTask.agility).isEqualTo(fetchedTask.agility)
		assertThat(savedTask.strength).isEqualTo(fetchedTask.strength)
		assertThat(savedTask.intelligence).isEqualTo(fetchedTask.intelligence)
		assertThat(savedTask.topics).containsExactlyInAnyOrderElementsOf(fetchedTask.topics)
		assertThat(savedTask.createdAt).isEqualTo(fetchedTask.createdAt)
		assertThat(savedTask.updatedAt).isEqualTo(fetchedTask.updatedAt)
	}
}
