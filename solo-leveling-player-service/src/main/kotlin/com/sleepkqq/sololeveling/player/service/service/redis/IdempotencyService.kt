package com.sleepkqq.sololeveling.player.service.service.redis

import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class IdempotencyService(
	private val redisTemplate: StringRedisTemplate
) {

	private val log = LoggerFactory.getLogger(javaClass)

	private companion object {
		const val ID_KEY_PREFIX = "tx:solo-leveling-player:"
		const val TTL_HOURS = 1L
	}

	fun isProcessed(transactionId: String): Boolean {
		val key = "$ID_KEY_PREFIX$transactionId"
		val isSet = redisTemplate.opsForValue()
			.setIfAbsent(key, "processed", TTL_HOURS, TimeUnit.HOURS)

		return when (isSet) {
			true -> {
				log.info("New transaction processed and stored in Redis | transactionId={}", transactionId)
				true
			}

			false -> {
				log.warn("Transaction already exists in Redis | transactionId={}", transactionId)
				false
			}
		}
	}
}