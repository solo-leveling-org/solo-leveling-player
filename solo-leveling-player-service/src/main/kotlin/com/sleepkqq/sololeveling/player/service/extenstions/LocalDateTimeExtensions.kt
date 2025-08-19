package com.sleepkqq.sololeveling.player.service.extenstions

import com.google.protobuf.Timestamp
import java.time.LocalDateTime
import java.time.ZoneOffset

fun LocalDateTime.toTimestamp(): Timestamp {
	val instant = this.atZone(ZoneOffset.UTC).toInstant()
	return Timestamp.newBuilder()
		.setSeconds(instant.epochSecond)
		.setNanos(instant.nano)
		.build()
}
