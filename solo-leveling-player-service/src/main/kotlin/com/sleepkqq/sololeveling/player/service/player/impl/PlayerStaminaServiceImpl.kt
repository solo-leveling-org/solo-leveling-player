package com.sleepkqq.sololeveling.player.service.player.impl

import com.sleepkqq.sololeveling.config.interceptor.UserContextHolder
import com.sleepkqq.sololeveling.player.config.properties.PlayerLimitsProperties
import com.sleepkqq.sololeveling.player.exception.InsufficientStaminaException
import com.sleepkqq.sololeveling.player.model.entity.Immutables
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerStamina
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerStaminaFetcher
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerStaminaView
import com.sleepkqq.sololeveling.player.model.repository.player.PlayerStaminaRepository
import com.sleepkqq.sololeveling.player.service.player.PlayerStaminaService
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.Instant
import java.util.*
import kotlin.math.min

@Service
class PlayerStaminaServiceImpl(
	private val playerStaminaRepository: PlayerStaminaRepository,
	private val playerLimitsProperties: PlayerLimitsProperties
) : PlayerStaminaService {

	private val log = LoggerFactory.getLogger(javaClass)

	@Transactional(readOnly = true)
	override fun find(playerId: Long, fetcher: PlayerStaminaFetcher): PlayerStamina? =
		playerStaminaRepository.find(playerId, fetcher)

	@Transactional
	override fun update(stamina: PlayerStamina): PlayerStamina =
		playerStaminaRepository.save(stamina, SaveMode.UPDATE_ONLY)

	override fun initialize(): PlayerStamina = Immutables.createPlayerStamina {
		it.setId(UUID.randomUUID())
			.setCurrent(playerLimitsProperties.limits.free.stamina.max)
			.setRegenerating(false)
			.setLastRegeneratedAt(Instant.now())
	}

	override fun consumeStamina(stamina: PlayerStamina, amount: Int): PlayerStamina {
		require(amount > 0) { "Stamina amount must be positive" }

		val currentStamina = calculateCurrentStamina(stamina)
		if (currentStamina.current() < amount) {
			throw InsufficientStaminaException(amount, currentStamina.current())
		}

		val staminaConfig = playerLimitsProperties.limits.free.stamina
		val wasAtMax = currentStamina.current() >= staminaConfig.max
		val newStamina = currentStamina.current() - amount

		return Immutables.createPlayerStamina(currentStamina) {
			it.setCurrent(newStamina)
				.setRegenerating(newStamina < staminaConfig.max)

			if (wasAtMax) {
				it.setLastRegeneratedAt(Instant.now())
			}
		}
	}

	override fun restoreStamina(stamina: PlayerStamina, amount: Int): PlayerStamina {
		require(amount > 0) { "Restore amount must be positive" }

		val currentStamina = calculateCurrentStamina(stamina)
		val staminaConfig = playerLimitsProperties.limits.free.stamina
		val newStamina = min(currentStamina.current() + amount, staminaConfig.max)

		return Immutables.createPlayerStamina(currentStamina) {
			it.setCurrent(newStamina)
				.setRegenerating(newStamina < staminaConfig.max)
		}
	}

	override fun fullRestore(stamina: PlayerStamina): PlayerStamina =
		Immutables.createPlayerStamina(stamina) {
			it.setCurrent(playerLimitsProperties.limits.free.stamina.max)
				.setRegenerating(false)
				.setLastRegeneratedAt(Instant.now())
		}

	override fun calculateCurrentStamina(stamina: PlayerStamina): PlayerStamina {
		val staminaConfig = playerLimitsProperties.limits.free.stamina
		val maxStamina = staminaConfig.max

		if (stamina.current() >= maxStamina) {
			return if (stamina.regenerating()) {
				Immutables.createPlayerStamina(stamina) {
					it.setRegenerating(false)
						.setLastRegeneratedAt(Instant.now())
				}
			} else {
				stamina
			}
		}

		val now = Instant.now()
		val secondsElapsed = Duration.between(stamina.lastRegeneratedAt(), now).seconds
		val intervalsCompleted = secondsElapsed / staminaConfig.regenIntervalSeconds

		if (intervalsCompleted == 0L) {
			return stamina
		}

		val staminaToRecover = (intervalsCompleted * staminaConfig.regenRate).toInt()
		val newStamina = min(stamina.current() + staminaToRecover, maxStamina)

		if (newStamina == stamina.current()) {
			return stamina
		}

		val completedSeconds = intervalsCompleted * staminaConfig.regenIntervalSeconds

		val newLastRegeneratedAt = if (newStamina >= maxStamina) {
			now
		} else {
			stamina.lastRegeneratedAt().plusSeconds(completedSeconds)
		}

		log.info(
			"Regenerated stamina for player {}: {} -> {} (+{} in {} seconds)",
			UserContextHolder.getUserId(), stamina.current(), newStamina, staminaToRecover, secondsElapsed
		)

		return Immutables.createPlayerStamina(stamina) {
			it.setCurrent(newStamina)
				.setRegenerating(newStamina < maxStamina)
				.setLastRegeneratedAt(newLastRegeneratedAt)
		}
	}

	@Transactional
	override fun getCurrentStamina(playerId: Long): PlayerStaminaView {
		val stamina = get(playerId)
		val calculated = calculateCurrentStamina(stamina)
		return PlayerStaminaView(calculated)
	}
}
