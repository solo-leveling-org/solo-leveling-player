package com.soloist.player.service.task

import com.soloist.player.BaseTestClass
import com.soloist.player.model.entity.player.dto.PlayerTaskView
import com.soloist.player.model.entity.player.enums.Rarity
import com.soloist.player.model.entity.task.enums.TaskTopic
import com.soloist.player.service.player.PlayerTaskService
import com.soloist.proto.player.EnumFilter
import com.soloist.proto.player.Filter
import com.soloist.proto.player.RequestPaging
import com.soloist.proto.player.RequestQueryOptions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class PlayerTaskServiceTest : BaseTestClass() {

	@Autowired
	private lateinit var playerTaskService: PlayerTaskService

	@Test
	fun `success player task search test`() {
		// Given
		val user = createUser(2, "search-test")
		val player = user.player()!!
		val task = createTask(
			experience = 100,
			currencyReward = 50,
			rarity = Rarity.EPIC,
			agility = 5,
			strength = 10,
			intelligence = 3,
			topics = listOf(TaskTopic.PRODUCTIVITY, TaskTopic.NUTRITION)
		)

		val playerTask = createPlayerTask(task, player.id())
		playerTaskService.insertAll(listOf(playerTask))

		// When
		val options = RequestQueryOptions.newBuilder()
			.setFilter(
				Filter.newBuilder()
					.addEnumFilters(
						EnumFilter.newBuilder()
							.setField("rarity")
							.addValues(task.rarity()!!.name)
					)
					.addEnumFilters(
						EnumFilter.newBuilder()
							.setField("status")
							.addValues(playerTask.status()!!.name)
					)
			)
			.build()

		val paging = RequestPaging.newBuilder()
			.setPage(0)
			.setPageSize(1)
			.build()

		val searchedTasks = playerTaskService.searchView(
			player.id(),
			options,
			paging,
			PlayerTaskView::class
		)

		assertThat(searchedTasks.totalRowCount.toInt()).isEqualTo(1)
	}
}
