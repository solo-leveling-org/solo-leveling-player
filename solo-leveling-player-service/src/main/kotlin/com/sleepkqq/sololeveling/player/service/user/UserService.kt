package com.sleepkqq.sololeveling.player.service.user

import com.sleepkqq.sololeveling.player.model.entity.Fetchers
import com.sleepkqq.sololeveling.player.model.entity.user.User
import com.sleepkqq.sololeveling.player.model.entity.user.UserFetcher
import com.sleepkqq.sololeveling.player.exception.ModelNotFoundException
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

	fun findVersion(id: Long): Int?
	fun insert(user: User): User
	fun update(user: User): User
	fun upsert(user: User): User
	fun updateLocale(id: Long, locale: Locale)
}
