package com.sleepkqq.sololeveling.player.model.entity.task.enums

import org.babyfish.jimmer.sql.EnumItem

enum class TaskTopic {
	@EnumItem(ordinal = 0)
	PHYSICAL_ACTIVITY,

	@EnumItem(ordinal = 1)
	MENTAL_HEALTH,

	@EnumItem(ordinal = 2)
	EDUCATION,

	@EnumItem(ordinal = 3)
	CREATIVITY,

	@EnumItem(ordinal = 4)
	SOCIAL_SKILLS,

	@EnumItem(ordinal = 5)
	HEALTHY_EATING,

	@EnumItem(ordinal = 6)
	PRODUCTIVITY,

	@EnumItem(ordinal = 7)
	EXPERIMENTS,

	@EnumItem(ordinal = 8)
	ECOLOGY,

	@EnumItem(ordinal = 9)
	TEAMWORK;

	companion object {
		private val COMPATIBLE_TOPICS: Map<TaskTopic, Set<TaskTopic>> = mapOf(
			PHYSICAL_ACTIVITY to setOf(MENTAL_HEALTH, HEALTHY_EATING, ECOLOGY),
			MENTAL_HEALTH to setOf(PHYSICAL_ACTIVITY, CREATIVITY, SOCIAL_SKILLS),
			EDUCATION to setOf(CREATIVITY, PRODUCTIVITY, EXPERIMENTS),
			CREATIVITY to setOf(EDUCATION, MENTAL_HEALTH, SOCIAL_SKILLS),
			SOCIAL_SKILLS to setOf(MENTAL_HEALTH, CREATIVITY, TEAMWORK),
			HEALTHY_EATING to setOf(PHYSICAL_ACTIVITY, ECOLOGY),
			PRODUCTIVITY to setOf(EDUCATION, TEAMWORK),
			EXPERIMENTS to setOf(EDUCATION, ECOLOGY),
			ECOLOGY to setOf(PHYSICAL_ACTIVITY, HEALTHY_EATING, EXPERIMENTS),
			TEAMWORK to setOf(SOCIAL_SKILLS, PRODUCTIVITY)
		)
	}

	fun getCompatibleTopics(): Set<TaskTopic> {
		return COMPATIBLE_TOPICS.getOrDefault(this, setOf())
	}
}