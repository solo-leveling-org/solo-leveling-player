package com.soloist.player.job

import com.soloist.player.config.properties.JobsProperties
import org.slf4j.Logger
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.core.Ordered
import java.util.concurrent.atomic.AtomicBoolean

abstract class AbstractStartupJob(
	private val jobName: String,
	private val jobProperties: JobsProperties.JobProperties
) : Ordered {

	private val started = AtomicBoolean(false)

	protected abstract val log: Logger

	protected open val pageSize: Int
		get() = jobProperties.pageSize ?: 20

	protected open val delayMillis: Long
		get() = jobProperties.delay?.toMillis() ?: 500L

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
		try {
			log.info("{} started", jobName)
			runJob()
			log.info("{} finished successfully", jobName)

		} catch (e: Exception) {
			log.error("{} failed with unexpected exception", jobName, e)
		}
	}

	protected abstract fun runJob()
}
