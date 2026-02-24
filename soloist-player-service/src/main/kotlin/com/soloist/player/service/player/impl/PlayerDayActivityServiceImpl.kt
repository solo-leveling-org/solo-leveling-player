package com.soloist.player.service.player.impl

import com.soloist.player.model.entity.player.PlayerDayActivity
import com.soloist.player.model.repository.player.PlayerDayActivityRepository
import com.soloist.player.service.player.PlayerDayActivityService
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.YearMonth

@Service
class PlayerDayActivityServiceImpl(
	private val playerDayActivityRepository: PlayerDayActivityRepository
) : PlayerDayActivityService {

	@Transactional
	override fun insertIfAbsent(activity: PlayerDayActivity): PlayerDayActivity =
		playerDayActivityRepository.save(activity, SaveMode.INSERT_IF_ABSENT)

	@Transactional(readOnly = true)
	override fun getMonthlyActivity(playerId: Long, year: Int, month: Int): List<Int> {
		return playerDayActivityRepository.findByMonth(playerId, YearMonth.of(year, month))
			.map { it.dayOfMonth }
	}
}
