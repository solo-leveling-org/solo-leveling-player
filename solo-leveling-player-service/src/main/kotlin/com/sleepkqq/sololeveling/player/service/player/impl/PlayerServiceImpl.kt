package com.sleepkqq.sololeveling.player.service.player.impl

import com.sleepkqq.sololeveling.player.model.entity.player.Player
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerFetcher
import com.sleepkqq.sololeveling.player.model.repository.player.PlayerRepository
import com.sleepkqq.sololeveling.player.service.player.PlayerService
import org.babyfish.jimmer.View
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.reflect.KClass

@Service
class PlayerServiceImpl(
	private val playerRepository: PlayerRepository
) : PlayerService {

	@Transactional(readOnly = true)
	override fun find(id: Long, fetcher: PlayerFetcher): Player? =
		playerRepository.findNullable(id, fetcher)

	@Transactional(readOnly = true)
	override fun <V : View<Player>> findView(id: Long, viewType: KClass<V>): V? =
		playerRepository.findView(id, viewType.java)

	@Transactional
	override fun insert(player: Player): Player =
		playerRepository.save(player, SaveMode.INSERT_ONLY)

	@Transactional
	override fun update(player: Player): Player =
		playerRepository.save(player, SaveMode.UPDATE_ONLY)
}
