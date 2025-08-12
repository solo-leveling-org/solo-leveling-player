package com.sleepkqq.sololeveling.player.service.service.player

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerBalanceTransaction

interface PlayerBalanceTransactionService {

	fun insert(playerBalanceTransaction: PlayerBalanceTransaction): PlayerBalanceTransaction
}
