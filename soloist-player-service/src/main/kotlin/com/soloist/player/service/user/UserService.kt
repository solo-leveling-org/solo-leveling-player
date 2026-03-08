package com.soloist.player.service.user

import com.soloist.jimmer.predicate.filter.DateFilter
import com.soloist.player.exception.LeaderboardUserNotFoundException
import com.soloist.player.model.entity.Fetchers
import com.soloist.player.model.entity.user.User
import com.soloist.player.model.entity.user.UserFetcher
import com.soloist.player.exception.ModelNotFoundException
import com.soloist.player.model.entity.user.LeaderboardUser
import com.soloist.player.model.entity.user.UsersStats
import com.soloist.proto.common.LeaderboardType
import com.soloist.proto.common.RequestPaging
import com.soloist.proto.user.UserLocale
import org.babyfish.jimmer.Page
import org.babyfish.jimmer.View
import kotlin.reflect.KClass

interface UserService {

	fun find(id: Long, fetcher: UserFetcher = Fetchers.USER_FETCHER.allScalarFields()): User?
	fun get(id: Long, fetcher: UserFetcher = Fetchers.USER_FETCHER.allScalarFields()): User =
		find(id, fetcher) ?: throw ModelNotFoundException(User::class, id)

	fun <V : View<User>> findView(id: Long, viewType: KClass<V>): V?
	fun <V : View<User>> getView(id: Long, viewType: KClass<V>): V = findView(id, viewType)
		?: throw ModelNotFoundException(User::class, id)

	fun insert(user: User): User
	fun update(user: User): User
	fun upsert(user: User): User
	fun updateLocale(id: Long, locale: UserLocale)
	fun register(user: User): User
	fun getLeaderboardPage(
		type: LeaderboardType,
		range: DateFilter.DayRange,
		paging: RequestPaging
	): Page<LeaderboardUser>

	fun findLeaderboardUser(
		id: Long,
		type: LeaderboardType,
		range: DateFilter.DayRange
	): LeaderboardUser?

	fun getLeaderboardUser(
		id: Long,
		type: LeaderboardType,
		range: DateFilter.DayRange
	): LeaderboardUser = findLeaderboardUser(id, type, range)
		?: throw LeaderboardUserNotFoundException()

	fun getUsersStats(): UsersStats
	fun <V : View<User>> getUsers(paging: RequestPaging, viewType: KClass<V>): Page<V>
}
