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
		val taskId = UUID.randomUUID()
		val title = "Test Task Title"
		val description = "Test Task Description"
		val experience = 100
		val currencyReward = BigDecimal("50.75")
		val rarity = TaskRarity.EPIC
		val agility = 5
		val strength = 10
		val intelligence = 3
		val topics = listOf(TaskTopic.PRODUCTIVITY, TaskTopic.SOCIAL_SKILLS)
		val now = LocalDateTime.now()

		val initialTask = Task {
			id = taskId
			this.title = title
			this.description = description
			this.experience = experience
			this.currencyReward = currencyReward
			this.rarity = rarity
			this.agility = agility
			this.strength = strength
			this.intelligence = intelligence
			this.topics = topics
			createdAt = now
			updatedAt = now
		}

		// Act
		val savedTask = taskService.insert(initialTask)

		// Assert
		assertThat(savedTask.id).isEqualTo(taskId)
		assertThat(savedTask.title).isEqualTo(title)
		assertThat(savedTask.description).isEqualTo(description)
		assertThat(savedTask.experience).isEqualTo(experience)
		assertThat(savedTask.currencyReward).isEqualByComparingTo(currencyReward)
		assertThat(savedTask.rarity).isEqualTo(rarity)
		assertThat(savedTask.agility).isEqualTo(agility)
		assertThat(savedTask.strength).isEqualTo(strength)
		assertThat(savedTask.intelligence).isEqualTo(intelligence)
		assertThat(savedTask.topics).containsExactlyInAnyOrderElementsOf(topics)
		assertThat(savedTask.createdAt).isEqualTo(now)
		assertThat(savedTask.updatedAt).isEqualTo(now)

		val fetchedTask = taskService.get(taskId)
		assertThat(fetchedTask).usingRecursiveComparison().isEqualTo(savedTask)
	}
}
