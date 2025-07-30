package com.sleepkqq.sololeveling.player.model.repository.player

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerBalanceTransaction
import org.babyfish.jimmer.spring.repository.KRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PlayerBalanceTransactionRepository : KRepository<PlayerBalanceTransaction, UUID>
