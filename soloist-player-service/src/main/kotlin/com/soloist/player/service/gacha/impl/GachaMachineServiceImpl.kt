package com.soloist.player.service.gacha.impl

import com.soloist.player.exception.ModelNotFoundException
import com.soloist.player.model.entity.Immutables
import com.soloist.player.model.entity.gacha.GachaMachine
import com.soloist.player.model.entity.gacha.dto.GachaMachineInput
import com.soloist.player.model.repository.gacha.GachaMachineItemRepository
import com.soloist.player.model.repository.gacha.GachaMachineRepository
import com.soloist.player.service.gacha.GachaMachineService
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.UUID

@Service
class GachaMachineServiceImpl(
	private val gachaMachineRepository: GachaMachineRepository,
	private val gachaMachineItemRepository: GachaMachineItemRepository
) : GachaMachineService {

	private val log = LoggerFactory.getLogger(javaClass)

	@Transactional
	override fun create(input: GachaMachineInput, imageFileId: String): GachaMachine {
		require(input.costAmount > BigDecimal.ZERO) { "Cost amount must be positive" }
		require(input.pullCount > 0) { "Pull count must be positive" }

		input.imageFileId = imageFileId

		val machine = gachaMachineRepository.save(
			input.toEntity(),
			SaveMode.INSERT_ONLY
		)

		log.info("Created gacha machine id={}", machine.id())

		return machine
	}

	@Transactional
	override fun addItem(machineId: UUID, gearItemId: UUID, weight: Int) {
		require(weight > 0) { "Item weight must be positive" }

		if (!gachaMachineRepository.exists(machineId)) {
			throw ModelNotFoundException(GachaMachine::class, machineId)
		}

		gachaMachineItemRepository.save(
			Immutables.createGachaMachineItem {
				it.setGachaMachineId(machineId)
					.setGearItemId(gearItemId)
					.setWeight(weight)
			},
			SaveMode.INSERT_ONLY
		)

		log.info("Added gear item {} to gacha machine {} with weight {}", gearItemId, machineId, weight)
	}

	@Transactional
	override fun removeItem(machineId: UUID, gearItemId: UUID) {
		val deleted = gachaMachineItemRepository.deleteByMachineAndGearItem(machineId, gearItemId)

		if (deleted == 0) {
			throw ModelNotFoundException(GachaMachine::class, "machine=$machineId, gearItem=$gearItemId")
		}

		log.info("Removed gear item {} from gacha machine {}", gearItemId, machineId)
	}
}
