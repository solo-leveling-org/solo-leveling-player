package com.sleepkqq.sololeveling.player.service.task

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTaskTopic
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskRarity
import org.springframework.stereotype.Service
import java.util.Random
import kotlin.math.max

@Service
class DefineTaskRarityService {

	private companion object {
		val RANDOM = Random()

		const val COMMON_BASE_WEIGHT = 80.0
		const val UNCOMMON_BASE_WEIGHT = 15.0
		const val RARE_BASE_WEIGHT = 5.0

		const val EPIC_LEVEL_THRESHOLD = 40.0
		const val LEGENDARY_LEVEL_THRESHOLD = 60.0
		const val COMMON_DISABLE_LEVEL_THRESHOLD = 80.0

		const val UNCOMMON_WEIGHT_MULTIPLIER = 0.5
		const val RARE_WEIGHT_MULTIPLIER = 0.3
		const val EPIC_WEIGHT_MULTIPLIER = 0.2
		const val LEGENDARY_WEIGHT_MULTIPLIER = 0.1
	}

	fun define(topics: List<PlayerTaskTopic>): TaskRarity {
		require(topics.isNotEmpty()) { "topics size must be > 0" }
		val avgLevel = topics.map {
			val level = it.level()
			require(level != null) { "level" }
			level.level()
		}
			.average()

		return define(avgLevel)
	}

	private fun define(avgLevel: Double): TaskRarity {
		val weights = getWeights(avgLevel)
		require(weights.isNotEmpty()) { "Weights array cannot be empty" }

		val totalWeight = weights.sum()
		val normalizedWeights = DoubleArray(weights.size) { i ->
			(weights[i] / totalWeight) * 100
		}

		val randomValue = RANDOM.nextDouble() * 100
		var cumulativeWeight = 0.0

		for (i in 0..<normalizedWeights.size) {
			cumulativeWeight += normalizedWeights[i]
			if (randomValue < cumulativeWeight) {
				return TaskRarity.entries[i]
			}
		}

		return TaskRarity.COMMON
	}

	private fun getWeights(avgLevel: Double): DoubleArray {
		var commonWeight = max(0.0, COMMON_BASE_WEIGHT - avgLevel)
		val uncommonWeight = UNCOMMON_BASE_WEIGHT + (avgLevel * UNCOMMON_WEIGHT_MULTIPLIER)
		val rareWeight = RARE_BASE_WEIGHT + (avgLevel * RARE_WEIGHT_MULTIPLIER)

		val epicWeight = if (avgLevel >= EPIC_LEVEL_THRESHOLD) {
			(avgLevel * EPIC_WEIGHT_MULTIPLIER)
		} else {
			0.0
		}
		val legendaryWeight = if (avgLevel >= LEGENDARY_LEVEL_THRESHOLD) {
			(avgLevel * LEGENDARY_WEIGHT_MULTIPLIER)
		} else {
			0.0
		}

		if (avgLevel >= COMMON_DISABLE_LEVEL_THRESHOLD) {
			commonWeight = 0.0
		}

		return doubleArrayOf(commonWeight, uncommonWeight, rareWeight, epicWeight, legendaryWeight)
	}
}
