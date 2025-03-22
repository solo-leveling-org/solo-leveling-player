package com.sleepkqq.sololeveling.player.service.repository;

import com.sleepkqq.sololeveling.player.service.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {

}
