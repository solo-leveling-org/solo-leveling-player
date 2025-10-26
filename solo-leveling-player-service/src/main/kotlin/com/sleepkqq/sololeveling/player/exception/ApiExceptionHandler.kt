package com.sleepkqq.sololeveling.player.exception

import io.grpc.Status
import io.grpc.StatusException
import org.slf4j.LoggerFactory
import org.springframework.grpc.server.exception.GrpcExceptionHandler
import org.springframework.stereotype.Component

@Suppress("unused")
@Component
class ApiExceptionHandler : GrpcExceptionHandler {

	private val log = LoggerFactory.getLogger(javaClass)

	override fun handleException(e: Throwable): StatusException {
		log.error("Unexpected error occurred", e)

		val status = when (e) {
			is ModelNotFoundException -> Status.NOT_FOUND
				.withDescription(e.message)

			is IllegalArgumentException -> Status.INVALID_ARGUMENT
				.withDescription(e.message)

			is IllegalStateException -> Status.FAILED_PRECONDITION
				.withDescription(e.message)

			else -> Status.INTERNAL
				.withDescription("Internal server error")
		}

		return status.withCause(e).asException()
	}
}
