package com.sleepkqq.sololeveling.player.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.retry.annotation.EnableRetry
import org.springframework.retry.backoff.ExponentialBackOffPolicy
import org.springframework.retry.policy.SimpleRetryPolicy
import org.springframework.retry.support.RetryTemplate

@EnableRetry
@Configuration
class RetryConfig {

	@Bean
	fun kafkaRetryTemplate(): RetryTemplate {
		val retryTemplate = RetryTemplate()

		val backOffPolicy = ExponentialBackOffPolicy()
		backOffPolicy.initialInterval = 1000L
		backOffPolicy.multiplier = 2.0
		backOffPolicy.maxInterval = 10000L
		retryTemplate.setBackOffPolicy(backOffPolicy)

		val retryPolicy = SimpleRetryPolicy()
		retryPolicy.maxAttempts = 3
		retryTemplate.setRetryPolicy(retryPolicy)

		return retryTemplate
	}
}
