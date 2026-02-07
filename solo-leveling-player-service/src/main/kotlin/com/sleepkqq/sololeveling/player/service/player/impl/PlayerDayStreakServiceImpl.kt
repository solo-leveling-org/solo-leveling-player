package com.sleepkqq.sololeveling.player.service.player.impl

import com.sleepkqq.sololeveling.player.model.entity.Immutables
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerDayStreak
import com.sleepkqq.sololeveling.player.service.player.PlayerDayStreakService
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.UUID
import kotlin.math.max

@Service
class PlayerDayStreakServiceImpl : PlayerDayStreakService {

	override fun extend(dayStreak: PlayerDayStreak): PlayerDayStreak {
		val zoneId = LocaleContextHolder.getTimeZone().toZoneId()
		val today = LocalDate.now(zoneId)
		val lastActiveDate = dayStreak.updatedAt().atZone(zoneId).toLocalDate()

		val daysDifference = ChronoUnit.DAYS.between(lastActiveDate, today)

		return when (daysDifference) {
			0L -> dayStreak
			1L -> Immutables.createPlayerDayStreak(dayStreak) {
				val updatedStreak = dayStreak.current() + 1
				it.setCurrent(updatedStreak)
				it.setMax(max(updatedStreak, dayStreak.max()))
			}

			else -> Immutables.createPlayerDayStreak(dayStreak) {
				it.setCurrent(1)
			}
		}
	}

	override fun initialize(): PlayerDayStreak = Immutables.createPlayerDayStreak {
		it.setId(UUID.randomUUID())
			.setCurrent(0)
			.setMax(0)
	}
}