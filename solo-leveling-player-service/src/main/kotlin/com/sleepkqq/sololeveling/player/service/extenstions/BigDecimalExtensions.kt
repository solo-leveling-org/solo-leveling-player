package com.sleepkqq.sololeveling.player.service.extenstions

import com.google.type.Money
import com.sleepkqq.sololeveling.player.model.entity.player.enums.CurrencyCode
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

fun BigDecimal.toMoney(currencyCode: CurrencyCode): Money {
	require(this.scale() <= 9) { "Scale cannot exceed 9 digits" }

	// Нормализуем к 9 знакам
	val normalized = this.setScale(9, RoundingMode.UNNECESSARY)

	// Переводим всё в нано-единицы как целое число
	val totalNanos = normalized.movePointRight(9).toBigIntegerExact()

	// Разделяем на units и nanos
	val units = totalNanos.divide(BigInteger.valueOf(1_000_000_000))
	val nanos = totalNanos.remainder(BigInteger.valueOf(1_000_000_000))

	// Проверяем диапазон nanos: должно быть в [-999_999_999, 999_999_999]
	require(nanos.abs() <= BigInteger.valueOf(999_999_999)) {
		"Nanos part out of range: ${nanos.toInt()}"
	}

	return Money.newBuilder()
		.setCurrencyCode(currencyCode.name)
		.setUnits(units.toLong())
		.setNanos(nanos.toInt())
		.build()
}