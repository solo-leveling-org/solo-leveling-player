package com.soloist.player.service.player.impl

import com.soloist.player.config.properties.PlayerLimitsProperties
import com.soloist.player.exception.InsufficientStaminaException
import com.soloist.player.model.entity.Immutables
import com.soloist.player.model.entity.player.PlayerStamina
import com.soloist.player.model.repository.player.PlayerStaminaRepository
import com.soloist.player.service.player.PlayerStaminaService
import org.babyfish.jimmer.View
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.Instant
import java.util.*
import kotlin.math.min
import kotlin.reflect.KClass

@Service
class PlayerStaminaServiceImpl(
	private val playerStaminaRepository: PlayerStaminaRepository,
	private val playerLimitsProperties: PlayerLimitsProperties
) : PlayerStaminaService {

	@Transactional(readOnly = true)
	override fun <V : View<PlayerStamina>> findView(playerId: Long, viewType: KClass<V>): V? =
		playerStaminaRepository.findView(playerId, viewType.java)

	@Transactional
	override fun update(stamina: PlayerStamina): PlayerStamina =
		playerStaminaRepository.save(stamina, SaveMode.UPDATE_ONLY)

	override fun initialize(): PlayerStamina = Immutables.createPlayerStamina {
		it.setId(UUID.randomUUID())
			.setCurrent(playerLimitsProperties.limits.free.stamina.max)
			.setRegenerating(false)
			.setLastRegeneratedAt(Instant.now())
	}

	override fun consume(stamina: PlayerStamina, amount: Int): PlayerStamina {
		require(amount > 0) { "Stamina amount must be positive" }

		val currentStamina = calculateCurrent(stamina)
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

	override fun restore(stamina: PlayerStamina, amount: Int): PlayerStamina {
		require(amount > 0) { "Restore amount must be positive" }

		val currentStamina = calculateCurrent(stamina)
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

	override fun calculateCurrent(stamina: PlayerStamina): PlayerStamina {
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

		return Immutables.createPlayerStamina(stamina) {
			it.setCurrent(newStamina)
				.setRegenerating(newStamina < maxStamina)
				.setLastRegeneratedAt(newLastRegeneratedAt)
		}
	}
}
