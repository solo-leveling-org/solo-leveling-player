package com.sleepkqq.sololeveling.player.service.redis

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

	fun isProcessed(txId: String): Boolean {
		val redisKey = "$ID_KEY_PREFIX$txId"

		val isNewTransaction = redisTemplate.opsForValue()
			.setIfAbsent(redisKey, "processed", TTL_HOURS, TimeUnit.HOURS)

		return when (isNewTransaction) {
			true -> {
				log.info("New transaction processed and stored in Redis | txId={}", txId)
				false
			}
			false -> {
				log.warn("Transaction already exists in Redis | txId={}", txId)
				true
			}

			null -> {
				log.error("Failed to get info about transaction | txId={}", txId)
				false
			}
		}
	}
}
