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
import org.springframework.util.StopWatch
import java.util.concurrent.atomic.AtomicBoolean

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
	private val started = AtomicBoolean(false)

	override fun getOrder(): Int = properties.initVectorTasks.order

	@EventListener(ApplicationReadyEvent::class)
	fun onApplicationReady() {
		if (!properties.initVectorTasks.enabled) {
			log.warn("Vector tasks init job is disabled")
			return
		}

		if (!started.compareAndSet(false, true)) {
			log.warn("Vector tasks init job already started, skipping")
			return
		}

		Thread {
			runJob()
		}.apply {
			name = "init-vector-tasks-job"
			isDaemon = true
			start()
		}
	}

	private fun runJob() {
		val jobWatch = StopWatch("init-vector-tasks")
		jobWatch.start("total")

		log.info(
			"Vector tasks init job started with pageSize={}, sleepMillis={}",
			PAGE_SIZE,
			SLEEP_MILLIS
		)

		var currentPage = 0
		var total = 0

		try {
			while (true) {
				val pageWatch = StopWatch("page-$currentPage")
				pageWatch.start("load-page")
				log.info("Loading tasks page={}, pageSize={}", currentPage, PAGE_SIZE)

				val tasksPage = taskService.findToVectorize(currentPage, PAGE_SIZE)
				pageWatch.stop()

				val rows = tasksPage.rows
				if (rows.isEmpty()) {
					log.info("No more tasks to vectorize. Stopping at page={}", currentPage)
					break
				}

				log.info(
					"Fetched {} tasks on page={} (totalPages={}), loadTimeMs={}",
					rows.size, currentPage, tasksPage.totalPageCount, pageWatch.totalTimeMillis
				)

				val tasks = rows.map(VectorizeTaskView::toEntity)

				try {
					log.info("Sending {} tasks to vectorStore on page={}", tasks.size, currentPage)
					taskVectorService.addTasks(tasks)

					total += tasks.size

					log.info("Page={} processed successfully.  accumulatedTotal={}", currentPage, total)

				} catch (e: Exception) {
					if (pageWatch.isRunning) {
						pageWatch.stop()
					}
					log.error(
						"Error while vectorizing tasks on page={}. Page timing: {}. Stopping job.",
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
					Thread.sleep(SLEEP_MILLIS)

				} catch (ie: InterruptedException) {
					Thread.currentThread().interrupt()
					log.warn("Vector tasks init job interrupted during sleep after page={}", currentPage, ie)
					break
				}

				currentPage++
			}

			if (jobWatch.isRunning) {
				jobWatch.stop()
			}
			log.info(
				"Vector tasks init job finished. Total vectorized tasks={}, totalPagesProcessed={}, totalTimeMs={}, details={}",
				total, currentPage + 1, jobWatch.totalTimeMillis, jobWatch.prettyPrint()
			)

		} catch (e: Exception) {
			if (jobWatch.isRunning) {
				jobWatch.stop()
			}
			log.error(
				"Vector tasks init job failed with unexpected exception after processing {} tasks. Timing: {}",
				total, jobWatch.prettyPrint(), e
			)
		}
	}
}
