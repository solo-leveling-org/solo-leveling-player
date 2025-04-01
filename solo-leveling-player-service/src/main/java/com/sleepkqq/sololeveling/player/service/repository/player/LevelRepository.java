package com.sleepkqq.sololeveling.player.service.repository.player;

import static com.sleepkqq.sololeveling.player.service.model.Tables.LEVEL_TABLE;

import com.sleepkqq.sololeveling.player.service.model.player.Level;
import org.babyfish.jimmer.spring.repository.JRepository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface LevelRepository extends JRepository<Level, UUID> {

  @Transactional
  default Optional<Level> findByPlayerId(long playerId) {
    var table = LEVEL_TABLE;
    return sql().createQuery(table)
        .where(table.playerId().eq(playerId))
        .select(table)
        .fetchOptional();
  }

  @Transactional
  default Optional<Level> findByPlayerTaskTopicId(UUID topicId) {
    var table = LEVEL_TABLE;
    return sql().createQuery(table)
        .where(table.playerTaskTopicId().eq(topicId))
        .select(table)
        .fetchOptional();
  }
}