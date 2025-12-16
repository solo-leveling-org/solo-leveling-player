package com.sleepkqq.sololeveling.player.exception

import io.grpc.Status
import io.grpc.StatusException
import org.slf4j.LoggerFactory
import org.springframework.grpc.server.exception.GrpcExceptionHandler
import org.springframework.stereotype.Component

@Component
class ApiExceptionHandler : GrpcExceptionHandler {

	private val log = LoggerFactory.getLogger(javaClass)

	override fun handleException(e: Throwable): StatusException {
		val status = when (e) {
			is ModelNotFoundException,
			is LeaderboardUserNotFoundException -> {
				log.warn("Not found: {}", e.message)
				Status.NOT_FOUND.withDescription(e.message)
			}

			is IllegalArgumentException -> {
				log.warn("Invalid argument: {}", e.message, e)
				Status.INVALID_ARGUMENT.withDescription(e.message)
			}

			is IllegalStateException -> {
				log.error("Illegal state", e)
				Status.FAILED_PRECONDITION.withDescription(e.message)
			}

			is AccessDeniedException -> {
				log.warn("Access denied: {}", e.message)
				Status.PERMISSION_DENIED
			}

			else -> {
				log.error("Unexpected error occurred", e)
				Status.INTERNAL.withDescription("Internal server error")
			}
		}

		return status.withCause(e).asException()
	}
}
