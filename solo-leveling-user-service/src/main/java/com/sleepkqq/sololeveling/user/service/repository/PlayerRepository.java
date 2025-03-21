package com.sleepkqq.sololeveling.user.service.repository;

import com.sleepkqq.sololeveling.user.service.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {

}
