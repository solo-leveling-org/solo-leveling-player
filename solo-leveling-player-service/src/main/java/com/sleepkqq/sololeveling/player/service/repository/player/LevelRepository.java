package com.sleepkqq.sololeveling.player.service.repository.player;

import com.sleepkqq.sololeveling.player.service.model.player.Level;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LevelRepository extends JpaRepository<Level, UUID> {

  Optional<Level> findByPlayerId(long playerId);

  Optional<Level> findByPlayerTaskTopicId(UUID topicId);
}
