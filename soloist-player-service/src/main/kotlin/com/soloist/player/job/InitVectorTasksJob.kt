package com.soloist.player.job

import com.soloist.player.config.properties.JobsProperties
import com.soloist.player.model.entity.task.dto.VectorizeTaskView
import com.soloist.player.service.ai.TaskVectorService
import com.soloist.player.service.task.TaskService
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.core.Ordered
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class InitVectorTasksJob(
	private val properties: JobsProperties,
	private val taskVectorService: TaskVectorService,
	private val taskService: TaskService
) : Ordered {

	private companion object {
		const val PAGE_SIZE = 20
		const val SLEEP_MILLIS = 500L
	}

	private val log = LoggerFactory.getLogger(javaClass)

	override fun getOrder(): Int = properties.initVectorTasks.order

	@Transactional
	@EventListener(ApplicationReadyEvent::class)
	fun call() {
		if (!properties.initVectorTasks.enabled) {
			log.warn("Vector tasks init job is disabled")
			return
		}

		log.info("Starting vector tasks init job")

		var currentPage = 0
		var total = 0

		try {
			do {
				val tasksPage = taskService.findToVectorize(currentPage, PAGE_SIZE)

				if (tasksPage.rows.isEmpty()) {
					break
				}

				taskVectorService.addTasks(tasksPage.rows.map(VectorizeTaskView::toEntity))
				total += tasksPage.rows.size

				try {
					Thread.sleep(SLEEP_MILLIS)
				} catch (ie: InterruptedException) {
					Thread.currentThread().interrupt()
					log.warn("Vector tasks init job interrupted during sleep", ie)
					break
				}

				currentPage++
			} while (currentPage < tasksPage.totalPageCount)

			log.info("Successfully initialized vector tasks: {}", total)

		} catch (e: Exception) {
			log.error("Error while initializing vector tasks", e)
		}
	}
}
