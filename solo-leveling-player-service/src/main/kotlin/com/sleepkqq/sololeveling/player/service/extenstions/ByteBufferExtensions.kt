package com.sleepkqq.sololeveling.player.service.extenstions

import java.math.BigDecimal
import java.math.BigInteger
import java.nio.ByteBuffer

fun ByteBuffer.toBigDecimal(scale: Int = 2): BigDecimal {
	val bytes = ByteArray(this.remaining())
	this.get(bytes)

	val unscaledValue = BigInteger(bytes)

	return BigDecimal(unscaledValue, scale)
}