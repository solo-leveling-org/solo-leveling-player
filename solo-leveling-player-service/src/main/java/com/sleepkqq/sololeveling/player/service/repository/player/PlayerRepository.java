package com.sleepkqq.sololeveling.player.service.repository.player;

import com.sleepkqq.sololeveling.player.service.model.player.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {

}
