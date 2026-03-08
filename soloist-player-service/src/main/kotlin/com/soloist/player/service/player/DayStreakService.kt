package com.soloist.player.service.player

import com.soloist.player.exception.ModelNotFoundException
import com.soloist.player.model.entity.player.DayStreak
import org.babyfish.jimmer.View
import java.time.LocalDate
import kotlin.reflect.KClass

interface DayStreakService {

	fun <V : View<DayStreak>> findView(playerId: Long, viewType: KClass<V>): V?
	fun <V : View<DayStreak>> getView(playerId: Long, viewType: KClass<V>): V =
		findView(playerId, viewType) ?: throw ModelNotFoundException(DayStreak::class, playerId)

	fun extend(dayStreak: DayStreak, today: LocalDate = LocalDate.now()): DayStreak
	fun initialize(): DayStreak
	fun update(dayStreak: DayStreak): DayStreak
	fun processStreak(playerId: Long, today: LocalDate = LocalDate.now()): DayStreak
	fun resetExpiredStreaks(): Long
}
