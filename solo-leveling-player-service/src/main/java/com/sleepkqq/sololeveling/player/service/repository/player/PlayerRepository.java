package com.sleepkqq.sololeveling.player.service.repository.player;

import static com.sleepkqq.sololeveling.player.service.model.Fetchers.LEVEL_FETCHER;
import static com.sleepkqq.sololeveling.player.service.model.Fetchers.PLAYER_FETCHER;
import static com.sleepkqq.sololeveling.player.service.model.Fetchers.PLAYER_TASK_TOPIC_FETCHER;
import static com.sleepkqq.sololeveling.player.service.model.Tables.PLAYER_TABLE;

import com.sleepkqq.sololeveling.player.service.model.player.Player;
import java.util.Optional;
import org.babyfish.jimmer.spring.repository.JRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PlayerRepository extends JRepository<Player, Long> {

  @Transactional
  default Optional<Player> findById(long id) {
    var table = PLAYER_TABLE;
    return sql().createQuery(table)
        .where(table.id().eq(id))
        .select(table.fetch(PLAYER_FETCHER
            .allScalarFields()
            .taskTopics(PLAYER_TASK_TOPIC_FETCHER
                .allScalarFields()
                .level(LEVEL_FETCHER.allScalarFields())
            )
            .level(LEVEL_FETCHER.allScalarFields())
        ))
        .fetchOptional();
  }
}
