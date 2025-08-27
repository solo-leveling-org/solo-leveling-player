package com.sleepkqq.sololeveling.player.model.repository.player;

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerBalance;
import java.util.UUID;
import org.babyfish.jimmer.spring.repository.JRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerBalanceRepository extends JRepository<PlayerBalance, UUID> {

}
