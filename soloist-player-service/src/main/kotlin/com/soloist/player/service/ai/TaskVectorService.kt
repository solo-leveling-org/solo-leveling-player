package com.soloist.player.service.ai

import com.soloist.player.model.entity.task.Task
import com.soloist.player.model.entity.task.Task.RARITY_FIELD
import com.soloist.player.model.entity.task.Task.TOPICS_FIELD
import com.soloist.player.model.entity.task.enums.TaskTopic
import com.soloist.player.model.repository.task.VectorTaskRepository
import org.springframework.ai.document.Document
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder
import org.springframework.ai.vectorstore.pgvector.PgVectorStore
import org.springframework.resilience.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TaskVectorService(
	private val vectorStore: PgVectorStore,
	private val vectorTaskRepository: VectorTaskRepository
) {

	@Retryable(maxRetries = 3, delay = 1000, multiplier = 2.0, jitter = 200)
	@Transactional
	fun addTasks(tasks: Collection<Task>) {
		val documents = tasks.map {
			val content = buildString {
				append("Title: ").append(it.title()!!.en())
				append("\n")
				append("Description: ").append(it.description()!!.en())
			}

			Document(
				it.id().toString(),
				content,
				mapOf(
					RARITY_FIELD to it.rarity().name,
					TOPICS_FIELD to it.topics().map { t -> t.topic().name }
				)
			)
		}

		vectorStore.add(documents)
	}

	@Transactional
	fun delete(taskTopic: TaskTopic) {
		val expr = FilterExpressionBuilder()
			.`in`("topics", listOf(taskTopic.name))
			.build()

		vectorStore.delete(expr)
	}

	@Transactional
	fun deleteAll(): Int = vectorTaskRepository.deleteAll()
}
