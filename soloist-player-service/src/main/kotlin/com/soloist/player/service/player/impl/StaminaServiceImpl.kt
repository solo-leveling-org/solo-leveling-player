package com.soloist.player.service.player.impl

import com.soloist.player.config.properties.PlayerLimitsProperties
import com.soloist.player.exception.InsufficientStaminaException
import com.soloist.player.model.entity.Immutables
import com.soloist.player.model.entity.player.Stamina
import com.soloist.player.model.repository.player.StaminaRepository
import com.soloist.player.service.player.StaminaService
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
class StaminaServiceImpl(
	private val staminaRepository: StaminaRepository,
	private val playerLimitsProperties: PlayerLimitsProperties
) : StaminaService {

	@Transactional(readOnly = true)
	override fun <V : View<Stamina>> findView(playerId: Long, viewType: KClass<V>): V? =
		staminaRepository.findView(playerId, viewType.java)

	@Transactional
	override fun update(stamina: Stamina): Stamina =
		staminaRepository.save(stamina, SaveMode.UPDATE_ONLY)

	override fun initialize(): Stamina = Immutables.createStamina {
		it.setId(UUID.randomUUID())
			.setCurrent(playerLimitsProperties.limits.free.stamina.max)
			.setRegenerating(false)
			.setLastRegeneratedAt(Instant.now())
	}

	override fun consume(stamina: Stamina, amount: Int): Stamina {
		require(amount > 0) { "Stamina amount must be positive" }

		val currentStamina = calculateCurrent(stamina)
		if (currentStamina.current() < amount) {
			throw InsufficientStaminaException(amount, currentStamina.current())
		}

		val staminaConfig = playerLimitsProperties.limits.free.stamina
		val wasAtMax = currentStamina.current() >= staminaConfig.max
		val newStamina = currentStamina.current() - amount

		return Immutables.createStamina(currentStamina) {
			it.setCurrent(newStamina)
				.setRegenerating(newStamina < staminaConfig.max)

			if (wasAtMax) {
				it.setLastRegeneratedAt(Instant.now())
			}
		}
	}

	override fun restore(stamina: Stamina, amount: Int): Stamina {
		require(amount > 0) { "Restore amount must be positive" }

		val currentStamina = calculateCurrent(stamina)
		val staminaConfig = playerLimitsProperties.limits.free.stamina
		val newStamina = min(currentStamina.current() + amount, staminaConfig.max)

		return Immutables.createStamina(currentStamina) {
			it.setCurrent(newStamina)
				.setRegenerating(newStamina < staminaConfig.max)
		}
	}

	override fun fullRestore(stamina: Stamina): Stamina =
		Immutables.createStamina(stamina) {
			it.setCurrent(playerLimitsProperties.limits.free.stamina.max)
				.setRegenerating(false)
				.setLastRegeneratedAt(Instant.now())
		}

	override fun calculateCurrent(stamina: Stamina): Stamina {
		val staminaConfig = playerLimitsProperties.limits.free.stamina
		val maxStamina = staminaConfig.max

		if (stamina.current() >= maxStamina) {
			return if (stamina.regenerating()) {
				Immutables.createStamina(stamina) {
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

		return Immutables.createStamina(stamina) {
			it.setCurrent(newStamina)
				.setRegenerating(newStamina < maxStamina)
				.setLastRegeneratedAt(newLastRegeneratedAt)
		}
	}
}
