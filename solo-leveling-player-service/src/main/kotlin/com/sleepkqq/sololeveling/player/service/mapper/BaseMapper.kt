package com.sleepkqq.sololeveling.player.service.mapper

import com.google.protobuf.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

abstract class BaseMapper {

	fun map(timestamp: Timestamp): LocalDateTime {
		val instant = Instant.ofEpochSecond(timestamp.seconds, timestamp.nanos.toLong())
		return LocalDateTime.ofInstant(instant, ZoneOffset.UTC)
	}

	fun map(localDateTime: LocalDateTime): Timestamp {
		val instant = localDateTime.toInstant(ZoneOffset.UTC)
		return Timestamp.newBuilder()
			.setSeconds(instant.epochSecond)
			.setNanos(instant.nano)
			.build()
	}

	fun map(string: String): UUID {
		return UUID.fromString(string)
	}

	fun map(uuid: UUID): String {
		return uuid.toString()
	}
}
