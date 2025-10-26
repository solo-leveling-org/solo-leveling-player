package com.sleepkqq.sololeveling.player.config

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Suppress("unused")
@Configuration
class CoroutineConfig {

	@Bean
	fun loomDispatcher(): CoroutineDispatcher = Executors.newVirtualThreadPerTaskExecutor()
		.asCoroutineDispatcher()
		.also {
			Runtime.getRuntime()
				.addShutdownHook(
					Thread { (it.executor as ExecutorService).shutdown() }
				)
		}
}
