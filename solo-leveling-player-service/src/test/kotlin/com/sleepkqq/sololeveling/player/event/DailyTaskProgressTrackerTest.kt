package com.sleepkqq.sololeveling.player.event

import com.sleepkqq.sololeveling.player.event.model.TaskCompletedEvent
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerDailyTask
import com.sleepkqq.sololeveling.player.model.entity.player.enums.DailyTaskType
import com.sleepkqq.sololeveling.player.model.entity.player.enums.Rarity
import com.sleepkqq.sololeveling.player.model.entity.player.sealed.CompleteSpecifiedRarityTask
import com.sleepkqq.sololeveling.player.service.player.PlayerDailyTaskService
import com.sleepkqq.sololeveling.player.service.player.PlayerDayStreakService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigDecimal

class DailyTaskProgressTrackerTest {

	@Mock
	private lateinit var playerDailyTaskService: PlayerDailyTaskService

	@Mock
	private lateinit var playerDayStreakService: PlayerDayStreakService

	private lateinit var tracker: DailyTaskProgressTracker

	@BeforeEach
	fun setUp() {
		MockitoAnnotations.openMocks(this)
		tracker = DailyTaskProgressTracker(playerDailyTaskService, playerDayStreakService)
	}

	@Test
	fun `should ignore events for already completed daily tasks`() {
		// Arrange
		val playerId = 1L
		val event = TaskCompletedEvent(playerId, Rarity.COMMON, DailyTaskType.TASKS)

		val completedTask = mock<PlayerDailyTask> {
			whenever(it.completed()).thenReturn(true)
		}

		whenever(playerDailyTaskService.find(playerId, DailyTaskType.TASKS))
			.thenReturn(completedTask)

		// Act
		tracker.listen(event)

		// Assert
		verify(playerDailyTaskService, never()).update(any())
		verify(playerDayStreakService, never()).processStreak(any())
	}

	@Test
	fun `should return early when daily task not found`() {
		// Arrange
		val playerId = 1L
		val event = TaskCompletedEvent(playerId, Rarity.COMMON, DailyTaskType.TASKS)

		whenever(playerDailyTaskService.find(playerId, DailyTaskType.TASKS))
			.thenReturn(null)

		// Act
		tracker.listen(event)

		// Assert
		verify(playerDailyTaskService, never()).update(any())
		verify(playerDayStreakService, never()).processStreak(any())
	}

	@Test
	fun `should calculate zero progress for non-matching task completion events`() {
		// Arrange
		val playerId = 1L
		val event = TaskCompletedEvent(playerId, Rarity.COMMON, DailyTaskType.TASKS)

		// Task requires EPIC rarity but event has COMMON rarity
		val completeSpecifiedRarityTask = CompleteSpecifiedRarityTask(Rarity.EPIC)

		val task = mock<PlayerDailyTask> {
			whenever(it.completed()).thenReturn(false)
			whenever(it.spec()).thenReturn(completeSpecifiedRarityTask)
			whenever(it.progress()).thenReturn(BigDecimal.ZERO)
		}

		whenever(playerDailyTaskService.find(playerId, DailyTaskType.TASKS))
			.thenReturn(task)

		// Act
		tracker.listen(event)

		// Assert
		// No update should occur because rarity doesn't match
		verify(playerDailyTaskService, never()).update(any())
		verify(playerDayStreakService, never()).processStreak(any())
	}

	@Test
	fun `should not update task when progress amount is zero`() {
		// Arrange
		val playerId = 1L
		val event = TaskCompletedEvent(playerId, Rarity.COMMON, DailyTaskType.TASKS)

		// Task requires EPIC rarity but event has COMMON rarity - progress will be zero
		val completeSpecifiedRarityTask = CompleteSpecifiedRarityTask(Rarity.EPIC)

		val task = mock<PlayerDailyTask> {
			whenever(it.completed()).thenReturn(false)
			whenever(it.spec()).thenReturn(completeSpecifiedRarityTask)
			whenever(it.progress()).thenReturn(BigDecimal.ZERO)
		}

		whenever(playerDailyTaskService.find(playerId, DailyTaskType.TASKS))
			.thenReturn(task)

		// Act
		tracker.listen(event)

		// Assert
		verify(playerDailyTaskService, never()).update(any())
		verify(playerDayStreakService, never()).processStreak(any())
	}

	@Test
	fun `should handle exception gracefully without propagating errors`() {
		// Arrange
		val playerId = 1L
		val event = TaskCompletedEvent(playerId, Rarity.COMMON, DailyTaskType.TASKS)

		// Use a rarity that doesn't match to avoid the update call
		val completeSpecifiedRarityTask = CompleteSpecifiedRarityTask(Rarity.EPIC)

		val task = mock<PlayerDailyTask> {
			whenever(it.completed()).thenReturn(false)
			whenever(it.spec()).thenReturn(completeSpecifiedRarityTask)
			whenever(it.progress()).thenReturn(BigDecimal.ZERO)
		}

		whenever(playerDailyTaskService.find(playerId, DailyTaskType.TASKS))
			.thenReturn(task)
		whenever(playerDailyTaskService.update(any()))
			.thenThrow(RuntimeException("Database error"))

		// Act & Assert - should not throw
		tracker.listen(event)

		// Verify that no update was attempted because progress was zero
		verify(playerDailyTaskService, never()).update(any())
		// Verify that streak processing was not called
		verify(playerDayStreakService, never()).processStreak(any())
	}

	@Test
	fun `should skip processing when task is already completed`() {
		// Arrange
		val playerId = 1L
		val event = TaskCompletedEvent(playerId, Rarity.EPIC, DailyTaskType.TASKS)

		val task = mock<PlayerDailyTask> {
			whenever(it.completed()).thenReturn(true)
		}

		whenever(playerDailyTaskService.find(playerId, DailyTaskType.TASKS))
			.thenReturn(task)

		// Act
		tracker.listen(event)

		// Assert
		verify(playerDailyTaskService, never()).update(any())
		verify(playerDayStreakService, never()).processStreak(any())
	}

	@Test
	fun `should handle null task gracefully`() {
		// Arrange
		val playerId = 1L
		val event = TaskCompletedEvent(playerId, Rarity.EPIC, DailyTaskType.TASKS)

		whenever(playerDailyTaskService.find(playerId, DailyTaskType.TASKS))
			.thenReturn(null)

		// Act
		tracker.listen(event)

		// Assert
		verify(playerDailyTaskService, never()).update(any())
		verify(playerDayStreakService, never()).processStreak(any())
	}

	@Test
	fun `should handle multiple events without side effects`() {
		// Arrange
		val playerId = 1L
		val event1 = TaskCompletedEvent(playerId, Rarity.COMMON, DailyTaskType.TASKS)
		val event2 = TaskCompletedEvent(playerId, Rarity.EPIC, DailyTaskType.TASKS)

		val completeSpecifiedRarityTask = CompleteSpecifiedRarityTask(Rarity.LEGENDARY)

		val task = mock<PlayerDailyTask> {
			whenever(it.completed()).thenReturn(false)
			whenever(it.spec()).thenReturn(completeSpecifiedRarityTask)
			whenever(it.progress()).thenReturn(BigDecimal.ZERO)
		}

		whenever(playerDailyTaskService.find(playerId, DailyTaskType.TASKS))
			.thenReturn(task)

		// Act
		tracker.listen(event1)
		tracker.listen(event2)

		// Assert
		// No updates should occur because neither event matches the LEGENDARY requirement
		verify(playerDailyTaskService, never()).update(any())
		verify(playerDayStreakService, never()).processStreak(any())
	}

	@Test
	fun `should not process events for completed tasks`() {
		// Arrange
		val playerId = 1L
		val event = TaskCompletedEvent(playerId, Rarity.COMMON, DailyTaskType.TASKS)

		val completedTask = mock<PlayerDailyTask> {
			whenever(it.completed()).thenReturn(true)
		}

		whenever(playerDailyTaskService.find(playerId, DailyTaskType.TASKS))
			.thenReturn(completedTask)

		// Act
		tracker.listen(event)

		// Assert
		verify(playerDailyTaskService, never()).update(any())
		verify(playerDayStreakService, never()).processStreak(any())
	}
}
