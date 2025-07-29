package com.sleepkqq.sololeveling.player.service.mapper

import com.google.protobuf.Timestamp
import org.mapstruct.Named
import java.time.LocalDateTime
import java.util.UUID

abstract class BaseMapper {

	@Named("uuidToString")
	fun map(uuid: UUID?): String? = uuid?.toString()

	@Named("stringToUuid")
	fun map(id: String?): UUID? = id?.let { UUID.fromString(it) }

	@Named("localDateTimeToTimestamp")
	fun map(localDateTime: LocalDateTime?): Timestamp? =
		localDateTime?.let {
			Timestamp.newBuilder()
				.setSeconds(it.atZone(java.time.ZoneOffset.UTC).toEpochSecond())
				.setNanos(it.nano)
				.build()
		}

	@Named("timestampToLocalDateTime")
	fun map(timestamp: Timestamp?): LocalDateTime? =
		timestamp?.let {
			LocalDateTime.ofEpochSecond(
				it.seconds,
				it.nanos,
				java.time.ZoneOffset.UTC
			)
		}
}
