package com.soloist.player.job

import com.soloist.player.config.properties.JobsProperties
import com.soloist.player.model.entity.task.dto.VectorizeTaskView
import com.soloist.player.service.ai.TaskVectorService
import com.soloist.player.service.task.TaskService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.util.StopWatch

@Service
class InitVectorTasksJob(
	properties: JobsProperties,
	private val taskVectorService: TaskVectorService,
	private val taskService: TaskService
) : AbstractStartupJob(
	jobProperties = properties.initVectorTasks
) {

	override val log: Logger = LoggerFactory.getLogger(javaClass)

	override fun runJob() {
		var currentPage = 0
		var total = 0

		while (true) {
			val pageWatch = StopWatch("page-$currentPage").apply { start("load-page") }

			log.info("Loading tasks page={}, pageSize={}", currentPage, pageSize)
			val tasksPage = taskService.findToVectorize(currentPage, pageSize)
			pageWatch.stop()

			val rows = tasksPage.rows
			if (rows.isEmpty()) {
				log.info("No more tasks to vectorize. Stop at page={}", currentPage)
				break
			}

			log.info(
				"Fetched {} tasks on page={} (totalPages={}), loadTimeMs={}",
				rows.size, currentPage, tasksPage.totalPageCount, pageWatch.totalTimeMillis
			)

			val tasks = rows.map(VectorizeTaskView::toEntity)

			try {
				log.info("Vectorizing {} tasks on page={}", tasks.size, currentPage)
				taskVectorService.addTasks(tasks)

				total += tasks.size
				log.info("Page={} done, accumulatedTotal={}", currentPage, total)

			} catch (e: Exception) {
				log.error(
					"Error while vectorizing page={}. Stopping job. Page timing: {}",
					currentPage, pageWatch.prettyPrint(), e
				)
				break
			}

			if (currentPage + 1 >= tasksPage.totalPageCount) {
				log.info(
					"Reached last page: currentPage={}, totalPageCount={}",
					currentPage, tasksPage.totalPageCount
				)
				break
			}

			try {
				Thread.sleep(delayMillis)
			} catch (ie: InterruptedException) {
				Thread.currentThread().interrupt()
				log.warn("Job interrupted during sleep after page={}", currentPage, ie)
				break
			}

			currentPage++
		}

		log.info("Vector tasks init job summary: totalTasks={}, pages={}", total, currentPage + 1)
	}
}
