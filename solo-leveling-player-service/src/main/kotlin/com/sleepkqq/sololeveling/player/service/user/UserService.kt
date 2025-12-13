package com.sleepkqq.sololeveling.player.service.user

import com.sleepkqq.sololeveling.jimmer.predicate.filter.DateFilter
import com.sleepkqq.sololeveling.player.exception.LeaderboardUserNotFoundException
import com.sleepkqq.sololeveling.player.model.entity.Fetchers
import com.sleepkqq.sololeveling.player.model.entity.user.User
import com.sleepkqq.sololeveling.player.model.entity.user.UserFetcher
import com.sleepkqq.sololeveling.player.exception.ModelNotFoundException
import com.sleepkqq.sololeveling.player.model.entity.user.LeaderboardUser
import com.sleepkqq.sololeveling.player.model.entity.user.UsersStats
import com.sleepkqq.sololeveling.proto.player.RequestPaging
import com.sleepkqq.sololeveling.proto.user.LeaderboardType
import org.babyfish.jimmer.Page
import org.babyfish.jimmer.View
import java.util.Locale
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
	fun updateLocale(id: Long, locale: Locale)
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
}
