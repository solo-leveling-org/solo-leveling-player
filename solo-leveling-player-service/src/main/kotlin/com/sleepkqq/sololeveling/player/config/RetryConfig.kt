package com.sleepkqq.sololeveling.player.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.retry.RetryPolicy
import org.springframework.core.retry.RetryTemplate
import java.time.Duration

@Configuration
class RetryConfig {

	@Bean
	fun kafkaRetryTemplate(): RetryTemplate = RetryTemplate(
		RetryPolicy.builder()
			.maxRetries(3)
			.delay(Duration.ofMillis(1000))
			.multiplier(2.0)
			.maxDelay(Duration.ofMillis(10000))
			.build()
	)
}
