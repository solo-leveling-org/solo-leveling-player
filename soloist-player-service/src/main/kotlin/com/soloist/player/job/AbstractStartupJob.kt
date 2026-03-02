package com.soloist.player.job

import com.soloist.player.config.properties.JobsProperties
import org.slf4j.Logger
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.core.Ordered
import org.springframework.util.StopWatch
import java.util.concurrent.atomic.AtomicBoolean

abstract class AbstractStartupJob(
	private val jobProperties: JobsProperties.JobProperties
) : Ordered {

	private val started = AtomicBoolean(false)

	protected abstract val log: Logger

	protected open val jobName: String = this::class.simpleName ?: "StartupJob"
	protected open val pageSize: Int = jobProperties.pageSize ?: 20
	protected open val delayMillis: Long = jobProperties.delay?.toMillis() ?: 500L

	override fun getOrder(): Int = jobProperties.order

	@EventListener(ApplicationReadyEvent::class)
	fun onApplicationReady() {
		if (!jobProperties.enabled) {
			log.warn("{} is disabled", jobName)
			return
		}

		if (!started.compareAndSet(false, true)) {
			log.warn("{} already started, skipping", jobName)
			return
		}

		Thread(::runSafely).apply {
			name = "$jobName-thread"
			isDaemon = true
			start()
		}
	}

	private fun runSafely() {
		val watch = StopWatch(jobName)
		watch.start("total")

		try {
			log.info("{} started", jobName)
			runJob()
			watch.stop()
			log.info("{} finished successfully, totalTimeMs={}", jobName, watch.totalTimeMillis)

		} catch (e: Exception) {
			if (watch.isRunning) {
				watch.stop()
			}
			log.error(
				"{} failed with unexpected exception, totalTimeMs={}",
				jobName, watch.totalTimeMillis, e
			)
		}
	}

	protected abstract fun runJob()
}
