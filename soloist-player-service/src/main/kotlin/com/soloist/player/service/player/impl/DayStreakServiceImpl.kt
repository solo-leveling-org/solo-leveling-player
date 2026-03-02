package com.soloist.player.service.player.impl

import com.soloist.player.kafka.producer.DayStreakExtendedProducer
import com.soloist.player.model.entity.Immutables
import com.soloist.player.model.entity.player.DayStreak
import com.soloist.player.model.entity.player.dto.ProcessDayStreakView
import com.soloist.player.model.repository.player.DayStreakRepository
import com.soloist.player.service.player.DayStreakService
import org.babyfish.jimmer.View
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.math.max
import kotlin.reflect.KClass

@Service
class DayStreakServiceImpl(
	private val dayStreakRepository: DayStreakRepository,
	private val dayStreakExtendedProducer: DayStreakExtendedProducer
) : DayStreakService {

	@Transactional(readOnly = true)
	override fun <V : View<DayStreak>> findView(playerId: Long, viewType: KClass<V>): V? =
		dayStreakRepository.findView(playerId, viewType.java)

	override fun extend(dayStreak: DayStreak, today: LocalDate): DayStreak {
		val lastActiveDate = dayStreak.updatedAt().atZone(ZoneOffset.UTC).toLocalDate()
		val daysDifference = ChronoUnit.DAYS.between(lastActiveDate, today)

		return when (daysDifference) {
			0L -> if (dayStreak.current() == 0) {
				Immutables.createDayStreak(dayStreak) {
					it.setCurrent(1)
						.setMax(1)
				}
			} else {
				dayStreak
			}

			1L -> Immutables.createDayStreak(dayStreak) {
				val updatedStreak = dayStreak.current() + 1
				it.setCurrent(updatedStreak)
					.setMax(max(updatedStreak, dayStreak.max()))
			}

			else -> Immutables.createDayStreak(dayStreak) {
				it.setCurrent(1)
					.setMax(max(1, dayStreak.max()))
			}
		}
	}

	override fun initialize(): DayStreak = Immutables.createDayStreak {
		it.setId(UUID.randomUUID())
			.setCurrent(0)
			.setMax(0)
	}

	@Transactional
	override fun update(dayStreak: DayStreak): DayStreak =
		dayStreakRepository.save(dayStreak, SaveMode.UPDATE_ONLY)

	@Transactional
	override fun processStreak(playerId: Long, today: LocalDate): DayStreak {
		val dayStreak = getView(playerId, ProcessDayStreakView::class)
		val extendedStreak = extend(dayStreak.toEntity(), today)

		if (extendedStreak.current() > dayStreak.current) {
			dayStreakExtendedProducer.send(userId = playerId)
		}

		return update(extendedStreak)
	}

	@Transactional
	override fun resetExpiredStreaks(): Long = dayStreakRepository.resetExpiredStreaks()
}