package com.sleepkqq.sololeveling.player.service.mapper

import com.sleepkqq.sololeveling.player.model.entity.player.Level
import com.sleepkqq.sololeveling.player.model.entity.player.Player
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTask
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTaskTopic
import com.sleepkqq.sololeveling.player.model.entity.task.Task
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic
import com.sleepkqq.sololeveling.player.model.entity.user.User
import com.sleepkqq.sololeveling.player.model.entity.user.enums.UserRole
import com.sleepkqq.sololeveling.proto.player.*
import com.sleepkqq.sololeveling.proto.user.UserInfo
import org.springframework.stereotype.Component

@Component
class ProtoMapper : BaseMapper() {

	fun map(taskTopic: TaskTopic): com.sleepkqq.sololeveling.proto.player.TaskTopic =
		taskTopic.name.let { com.sleepkqq.sololeveling.proto.player.TaskTopic.valueOf(it) }

	fun map(taskTopic: com.sleepkqq.sololeveling.proto.player.TaskTopic) =
		taskTopic.name.let { TaskTopic.valueOf(it) }

	fun map(assessment: com.sleepkqq.sololeveling.player.model.entity.player.enums.Assessment): Assessment =
		assessment.name.let { Assessment.valueOf(it) }

	fun map(playerTaskStatus: com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerTaskStatus): PlayerTaskStatus =
		playerTaskStatus.name.let { PlayerTaskStatus.valueOf(it) }

	fun map(playerTaskStatus: PlayerTaskStatus): com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerTaskStatus =
		playerTaskStatus.name.let {
			com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerTaskStatus.valueOf(
				it
			)
		}

	fun map(taskRarity: com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskRarity): TaskRarity =
		taskRarity.name.let { TaskRarity.valueOf(it) }

	fun map(taskRarity: TaskRarity): com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskRarity =
		taskRarity.name.let {
			com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskRarity.valueOf(
				it
			)
		}

	fun map(userRole: UserRole): com.sleepkqq.sololeveling.proto.user.UserRole =
		userRole.name.let { com.sleepkqq.sololeveling.proto.user.UserRole.valueOf(it) }

	fun map(userRole: com.sleepkqq.sololeveling.proto.user.UserRole): UserRole =
		userRole.name.let { UserRole.valueOf(it) }

	fun map(player: Player): PlayerInfo = player.let {
		PlayerInfo.newBuilder()
			.setId(it.id)
			.setMaxTasks(it.maxTasks)
			.addAllPlayerTaskTopicInfo(it.taskTopics.map { t -> map(t) })
			.build()
	}

	fun map(level: Level): LevelInfo = LevelInfo.newBuilder()
		.setId(map(level.id))
		.setLevel(level.level)
		.setTotalExperience(level.totalExperience)
		.setCurrentExperience(level.currentExperience)
		.setExperienceToNextLevel(level.experienceToNextLevel)
		.setAssessment(map(level.assessment))
		.build()

	fun map(playerTaskTopic: PlayerTaskTopic): PlayerTaskTopicInfo = PlayerTaskTopicInfo.newBuilder()
		.setId(map(playerTaskTopic.id))
		.setTaskTopic(map(playerTaskTopic.taskTopic))
		.setLevelInfo(playerTaskTopic.level?.let { map(it) })
		.build()

	fun map(playerTask: PlayerTask): PlayerTaskInfo = PlayerTaskInfo.newBuilder()
		.apply {
			id = map(playerTask.id)
			taskInfo = map(playerTask.task)
			status = map(playerTask.status)
			createdAt = map(playerTask.createdAt)
			playerTask.closedAt?.let { c -> closedAt = map(c) }
		}
		.build()

	fun map(playerTaskInfo: PlayerTaskInfo): PlayerTask = PlayerTask {
		id = map(playerTaskInfo.id)
		task = map(playerTaskInfo.taskInfo)
		status = map(playerTaskInfo.status)
		createdAt = map(playerTaskInfo.createdAt)
		closedAt = map(playerTaskInfo.closedAt)
	}

	fun map(task: Task): TaskInfo = TaskInfo.newBuilder()
		.apply {
			id = map(task.id)
			task.topics?.let { t -> addAllTopic(t.map { c -> map(c) }) }
			task.title?.let { title = it }
			task.description?.let { description = it }
			task.experience?.let { experience = it }
			task.rarity?.let { rarity = map(it) }
			task.agility?.let { agility = it }
			task.strength?.let { strength = it }
			task.intelligence?.let { intelligence = it }
		}
		.build()

	fun map(taskInfo: TaskInfo): Task = Task {
		id = map(taskInfo.id)
		topics = taskInfo.topicList.map { map(it) }
		title = taskInfo.title
		description = taskInfo.description
		experience = taskInfo.experience
		rarity = map(taskInfo.rarity)
		agility = taskInfo.agility
		strength = taskInfo.strength
		intelligence = taskInfo.intelligence
	}

	fun map(user: User): UserInfo = UserInfo.newBuilder()
		.setId(user.id)
		.setUsername(user.username)
		.setFirstName(user.firstName)
		.setLastName(user.lastName)
		.setPhotoUrl(user.photoUrl)
		.setLocale(user.locale)
		.addAllRole(user.roles?.map { map(it) })
		.build()

	fun map(userInfo: UserInfo): User = userInfo.let {
		User {
			id = it.id
			username = it.username
			firstName = it.firstName
			lastName = it.lastName
			photoUrl = it.photoUrl
			locale = it.locale
			roles = it.roleList.map { r -> map(r) }
		}
	}
}
