package com.sleepkqq.sololeveling.player.service.task

import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic
import org.springframework.stereotype.Service
import org.springframework.util.CollectionUtils
import java.util.Random

@Service
class DefineTaskTopicService {

	companion object {
		private val RANDOM = Random()

		const val MAX_TASK_TOPICS_COUNT = 2
	}

	fun define(topics: Set<TaskTopic>): List<TaskTopic> {
		if (CollectionUtils.isEmpty(topics)) {
			return listOf()
		}

		val topicsList = topics.toMutableList()
		topicsList.shuffle()

		if (oneTopic()) {
			return getFirstTopic(topicsList)
		}

		return topics.firstNotNullOfOrNull {
			it.compatibleTopics
				.firstOrNull { c -> topics.contains(c) }
				?.let { c -> listOf(it, c) }
		}
			?: getFirstTopic(topicsList)
	}

	private fun getFirstTopic(topicsList: List<TaskTopic>): List<TaskTopic> {
		return listOf(topicsList.first())
	}

	// 1 topic - 66.7% chance, 2 topics - 33.3% chance
	private fun oneTopic(): Boolean {
		return RANDOM.nextInt(3) < MAX_TASK_TOPICS_COUNT
	}
}
