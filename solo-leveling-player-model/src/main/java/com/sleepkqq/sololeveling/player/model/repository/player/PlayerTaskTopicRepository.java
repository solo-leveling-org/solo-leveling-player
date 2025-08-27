package com.sleepkqq.sololeveling.player.model.repository.player;

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTaskTopic;
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerTaskTopicView;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.babyfish.jimmer.spring.repository.JRepository;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerTaskTopicRepository extends JRepository<PlayerTaskTopic, UUID> {

  List<PlayerTaskTopicView> findByPlayerId(long playerId);

  default List<PlayerTaskTopic> updateAll(Collection<PlayerTaskTopic> entities) {
    return saveEntities(entities, SaveMode.UPDATE_ONLY);
  }
}
