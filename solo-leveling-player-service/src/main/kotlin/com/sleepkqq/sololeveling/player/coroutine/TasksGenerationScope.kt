package com.sleepkqq.sololeveling.player.coroutine

import jakarta.annotation.PreDestroy
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Suppress("unused")
@Component
class TaskGenerationScope(loomDispatcher: CoroutineDispatcher) {

	private val log = LoggerFactory.getLogger(javaClass)

	private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
		log.error("Unhandled exception in task generation coroutine", throwable)
	}

	val scope = CoroutineScope(SupervisorJob() + loomDispatcher + exceptionHandler)

	@PreDestroy
	fun destroy() {
		scope.cancel()
	}
}
