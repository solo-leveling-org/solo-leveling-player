package com.soloist.player.service.player

import com.soloist.player.exception.ModelNotFoundException
import com.soloist.player.model.entity.Fetchers
import com.soloist.player.model.entity.player.Player
import com.soloist.player.model.entity.player.PlayerFetcher
import org.babyfish.jimmer.View
import kotlin.reflect.KClass

interface PlayerService {

	fun find(id: Long, fetcher: PlayerFetcher = Fetchers.PLAYER_FETCHER.allScalarFields()): Player?
	fun get(id: Long, fetcher: PlayerFetcher = Fetchers.PLAYER_FETCHER.allScalarFields()): Player =
		find(id, fetcher) ?: throw ModelNotFoundException(Player::class, id)
	fun <V : View<Player>> findView(id: Long, viewType: KClass<V>): V?
	fun <V : View<Player>> getView(id: Long, viewType: KClass<V>): V = findView(id, viewType)
		?: throw ModelNotFoundException(Player::class, id)

	fun insert(player: Player): Player
	fun update(player: Player): Player
	fun initialize(userId: Long): Player
	fun reset(id: Long)
}
