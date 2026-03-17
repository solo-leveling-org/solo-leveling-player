package com.soloist.player.service.user.impl

import com.soloist.jimmer.predicate.filter.DateFilter
import com.soloist.player.model.entity.Immutables
import com.soloist.player.model.entity.user.LeaderboardUser
import com.soloist.player.model.entity.user.User
import com.soloist.player.model.entity.user.UserFetcher
import com.soloist.player.model.entity.user.UsersStats
import com.soloist.player.model.entity.user.dto.AuthUserView
import com.soloist.player.model.entity.user.dto.AuthUserView.TargetOf_roles
import com.soloist.player.model.entity.user.enums.UserRole
import com.soloist.player.model.repository.user.UserRepository
import com.soloist.player.service.player.PlayerService
import com.soloist.player.service.user.UserService
import com.soloist.proto.common.LeaderboardType
import com.soloist.proto.common.RequestPaging
import com.soloist.proto.user.UserLocale
import org.babyfish.jimmer.Page
import org.babyfish.jimmer.View
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID
import kotlin.reflect.KClass

@Service
class UserServiceImpl(
	private val userRepository: UserRepository,
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
	override fun upsert(user: User): User {
		val existingUser = findView(user.id(), AuthUserView::class)

		if (existingUser != null) {
			val updatedUser = Immutables.createUser(user) {
				it.setVersion(existingUser.version)
					.setManualLocale(existingUser.manualLocale)
					.setRoles(existingUser.roles.map(TargetOf_roles::toEntity))
					.setLastLoginAt(Instant.now())
			}
			return update(updatedUser)
		}

		return insert(register(user))
	}

	@Transactional
	override fun updateLocale(id: Long, locale: UserLocale) {
		userRepository.updateLocale(id, locale)
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

	@Transactional(readOnly = true)
	override fun <V : View<User>> getUsers(paging: RequestPaging, viewType: KClass<V>): Page<V> =
		userRepository.find(paging, viewType.java)
}
