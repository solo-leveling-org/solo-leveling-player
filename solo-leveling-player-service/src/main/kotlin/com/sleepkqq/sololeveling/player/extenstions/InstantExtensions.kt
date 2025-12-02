package com.sleepkqq.sololeveling.player.extenstions

import com.google.protobuf.Timestamp
import org.springframework.context.i18n.LocaleContextHolder
import java.time.Instant

fun Instant.toTimestamp(): Timestamp {
	val zoneId = LocaleContextHolder.getTimeZone().toZoneId()
	val zonedDateTime = this.atZone(zoneId)
	return Timestamp.newBuilder()
		.setSeconds(zonedDateTime.toEpochSecond())
		.setNanos(zonedDateTime.nano)
		.build()
}
