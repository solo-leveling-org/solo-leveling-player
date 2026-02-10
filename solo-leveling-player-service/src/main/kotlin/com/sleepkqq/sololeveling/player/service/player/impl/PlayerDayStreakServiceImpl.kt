package com.sleepkqq.sololeveling.player.service.player.impl

import com.sleepkqq.sololeveling.player.model.entity.Immutables
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerDayStreak
import com.sleepkqq.sololeveling.player.model.repository.player.PlayerDayStreakRepository
import com.sleepkqq.sololeveling.player.service.player.PlayerDayStreakService
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.math.max

@Service
class PlayerDayStreakServiceImpl(
	private val playerDayStreakRepository: PlayerDayStreakRepository
) : PlayerDayStreakService {

	@Transactional(readOnly = true)
	override fun find(playerId: Long): PlayerDayStreak? =
		playerDayStreakRepository.findNullable(playerId)

	override fun extend(dayStreak: PlayerDayStreak): PlayerDayStreak {
		val today = LocalDate.now(ZoneOffset.UTC)
		val lastActiveDate = dayStreak.updatedAt().atZone(ZoneOffset.UTC).toLocalDate()

		val daysDifference = ChronoUnit.DAYS.between(lastActiveDate, today)

		return when (daysDifference) {
			0L -> if (dayStreak.current() == 0) {
				Immutables.createPlayerDayStreak(dayStreak) {
					it.setCurrent(1)
						.setMax(1)
				}
			} else {
				dayStreak
			}

			1L -> Immutables.createPlayerDayStreak(dayStreak) {
				val updatedStreak = dayStreak.current() + 1
				it.setCurrent(updatedStreak)
					.setMax(max(updatedStreak, dayStreak.max()))
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

	@Transactional
	override fun update(dayStreak: PlayerDayStreak): PlayerDayStreak =
		playerDayStreakRepository.save(dayStreak, SaveMode.UPDATE_ONLY)

	@Transactional
	override fun processStreak(playerId: Long): PlayerDayStreak {
		val dayStreak = get(playerId)
		val extendedStreak = extend(dayStreak)
		return update(extendedStreak)
	}

	@Transactional
	override fun resetExpiredStreaks(): Long = playerDayStreakRepository.resetExpiredStreaks()
}