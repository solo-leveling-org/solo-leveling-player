package com.soloist.player.service.player.impl

import com.soloist.player.model.entity.player.DayActivity
import com.soloist.player.model.repository.player.DayActivityRepository
import com.soloist.player.service.player.DayActivityService
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.YearMonth

@Service
class DayActivityServiceImpl(
	private val dayActivityRepository: DayActivityRepository
) : DayActivityService {

	@Transactional
	override fun insertIfAbsent(activity: DayActivity): DayActivity =
		dayActivityRepository.save(activity, SaveMode.INSERT_IF_ABSENT)

	@Transactional(readOnly = true)
	override fun getMonthlyActivity(playerId: Long, year: Int, month: Int): List<Int> {
		return dayActivityRepository.findByMonth(playerId, YearMonth.of(year, month))
			.map { it.dayOfMonth }
	}
}
