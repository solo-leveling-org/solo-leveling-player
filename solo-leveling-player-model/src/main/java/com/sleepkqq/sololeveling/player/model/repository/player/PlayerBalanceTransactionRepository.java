package com.sleepkqq.sololeveling.player.model.repository.player;

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerBalanceTransaction;
import java.util.UUID;
import org.babyfish.jimmer.spring.repository.JRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerBalanceTransactionRepository extends
    JRepository<PlayerBalanceTransaction, UUID> {

}
