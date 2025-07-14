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
		return redisTemplate.opsForValue().setIfAbsent(key, "processed", TTL_HOURS, TimeUnit.HOURS)
			?: true.also {
				log.warn("Failed to check transactionId in Redis | transactionId={}", transactionId)
			}
	}
}