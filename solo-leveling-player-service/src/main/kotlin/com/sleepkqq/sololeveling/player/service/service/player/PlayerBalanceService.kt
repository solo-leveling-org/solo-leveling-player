package com.sleepkqq.sololeveling.player.service.service.player

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerBalance
import org.springframework.stereotype.Service

@Service
interface PlayerBalanceService {

	fun initializePlayerBalance(): PlayerBalance
}