package com.soloist.player.exception

class CustomTaskValidationException(val rejectionReason: String) :
	RuntimeException("Custom task rejected: $rejectionReason")
