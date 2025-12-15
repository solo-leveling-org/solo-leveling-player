package com.sleepkqq.sololeveling.player.service.user.impl

import com.sleepkqq.sololeveling.jimmer.predicate.filter.DateFilter
import com.sleepkqq.sololeveling.player.model.entity.Fetchers
import com.sleepkqq.sololeveling.player.model.entity.Immutables
import com.sleepkqq.sololeveling.player.model.entity.user.LeaderboardUser
import com.sleepkqq.sololeveling.player.model.entity.user.User
import com.sleepkqq.sololeveling.player.model.entity.user.UserFetcher
import com.sleepkqq.sololeveling.player.model.entity.user.UsersStats
import com.sleepkqq.sololeveling.player.model.entity.user.enums.UserRole
import com.sleepkqq.sololeveling.player.model.repository.user.UserRepository
import com.sleepkqq.sololeveling.player.service.notification.NotificationCommand
import com.sleepkqq.sololeveling.player.service.notification.NotificationService
import com.sleepkqq.sololeveling.player.service.player.PlayerService
import com.sleepkqq.sololeveling.player.service.user.UserService
import com.sleepkqq.sololeveling.proto.player.RequestPaging
import com.sleepkqq.sololeveling.proto.user.LeaderboardType
import org.babyfish.jimmer.Page
import org.babyfish.jimmer.View
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*
import kotlin.reflect.KClass

@Service
class UserServiceImpl(
	private val userRepository: UserRepository,
	private val notificationService: NotificationService,
	private val playerService: PlayerService
) : UserService {

	@Transactional(readOnly = true)
	override fun find(id: Long, fetcher: UserFetcher): User? =
		userRepository.findNullable(id, fetcher)

	@Transactional(readOnly = true)
	override fun <V : View<User>> findView(id: Long, viewType: KClass<V>): V? =
		userRepository.findView(id, viewType.java)

	@Transactional
	override fun insert(user: User): User = userRepository.save(user, SaveMode.INSERT_ONLY)

	@Transactional
	override fun update(user: User): User =
		userRepository.save(user, SaveMode.UPDATE_ONLY)

	@Transactional
	override fun upsert(user: User): User =
		find(
			user.id(),
			Fetchers.USER_FETCHER
				.version()
				.manualLocale()
				.roles(Fetchers.USER_ROLE_ITEM_FETCHER.allScalarFields())
		)
			?.let {
				update(
					Immutables.createUser(user) { u ->
						u.setVersion(it.version())
						u.setManualLocale(it.manualLocale())
						u.setRoles(it.roles())
						u.setLastLoginAt(Instant.now())
					}
				)
			}
			?: insert(register(user))

	@Transactional
	override fun updateLocale(id: Long, locale: Locale) {
		userRepository.updateLocale(id, locale)

		notificationService.send(NotificationCommand.UpdateLocale(id))
	}

	override fun register(user: User): User = Immutables.createUser(user) {
		it.setRoles(
			listOf(
				Immutables.createUserRoleItem { r ->
					r.setId(UUID.randomUUID())
					r.setRole(UserRole.USER)
				}
			)
		)
		it.setPlayer(playerService.initialize(user.id()))
	}

	@Transactional(readOnly = true)
	override fun getLeaderboardPage(
		type: LeaderboardType,
		range: DateFilter.DayRange,
		paging: RequestPaging
	): Page<LeaderboardUser> = userRepository.getLeaderboardPage(type, range, paging)

	@Transactional(readOnly = true)
	override fun findLeaderboardUser(
		id: Long,
		type: LeaderboardType,
		range: DateFilter.DayRange
	): LeaderboardUser? = userRepository.findLeaderboardUser(id, type, range)

	@Transactional(readOnly = true)
	override fun getUsersStats(): UsersStats = userRepository.getUsersStats()
}
