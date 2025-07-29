package com.sleepkqq.sololeveling.player.service.service.task

import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic
import org.springframework.stereotype.Service
import org.springframework.util.CollectionUtils
import java.util.Random

@Service
class DefineTaskTopicService {

	private companion object {
		val RANDOM = Random()
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
			it.getCompatibleTopics()
				.firstOrNull { c -> topics.contains(c) }
				?.let { c -> listOf(it, c) }
		}
			?: getFirstTopic(topicsList)
	}

	private fun getFirstTopic(topicsList: List<TaskTopic>): List<TaskTopic> {
		return listOf(topicsList.first())
	}

	private fun oneTopic(): Boolean {
		return RANDOM.nextInt(3) < 2
	}
}
