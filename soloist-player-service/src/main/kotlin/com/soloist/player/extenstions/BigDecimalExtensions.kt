package com.soloist.player.extenstions

import com.google.type.Decimal
import com.google.type.Money
import com.soloist.player.model.entity.player.enums.CurrencyCode
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

fun BigDecimal.toMoney(currencyCode: CurrencyCode? = null): Money {
	// Нормализуем к 2 знакам после запятой
	val normalized = this.setScale(2, RoundingMode.HALF_UP)

	// Переводим всё в нано-единицы как целое число
	// Для 2 знаков после запятой: 123.45 → 123450000000 nanos
	val totalNanos = normalized.movePointRight(9).toBigIntegerExact()

	// Разделяем на units и nanos
	val units = totalNanos.divide(BigInteger.valueOf(1_000_000_000))
	val nanos = totalNanos.remainder(BigInteger.valueOf(1_000_000_000))

	// Проверяем диапазон nanos: должно быть в [-999_999_999, 999_999_999]
	require(nanos.abs() <= BigInteger.valueOf(999_999_999)) {
		"Nanos part out of range: ${nanos.toInt()}"
	}

	val builder = Money.newBuilder()
		.setUnits(units.toLong())
		.setNanos(nanos.toInt())

	if (currencyCode != null) {
		builder.currencyCode = currencyCode.name
	}

	return builder.build()
}

fun BigDecimal.toGoogleDecimal(): Decimal = Decimal.newBuilder()
	.setValue(this.toPlainString())
	.build()

fun Number.toGoogleDecimal(): Decimal = when (this) {
	is BigDecimal -> this.toGoogleDecimal()
	else -> BigDecimal(this.toString()).toGoogleDecimal()
}
