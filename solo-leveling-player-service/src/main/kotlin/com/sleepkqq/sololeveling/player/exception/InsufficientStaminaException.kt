package com.sleepkqq.sololeveling.player.exception

class InsufficientStaminaException(required: Int, available: Int) :
	RuntimeException("Insufficient stamina: required '$required', but only '$available' available")
