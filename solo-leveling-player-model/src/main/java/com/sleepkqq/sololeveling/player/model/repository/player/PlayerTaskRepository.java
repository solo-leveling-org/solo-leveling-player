package com.sleepkqq.sololeveling.player.model.repository.player;

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTask;
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerTaskStatus;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.babyfish.jimmer.View;
import org.babyfish.jimmer.spring.repository.JRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerTaskRepository extends JRepository<PlayerTask, UUID> {

  <V extends View<PlayerTask>> List<V> findByPlayerIdAndStatusIn(
      long playerId,
      Collection<PlayerTaskStatus> statuses,
      Class<V> viewType
  );

  long countByPlayerIdAndStatusIn(long playerId, Collection<PlayerTaskStatus> statuses);

  List<PlayerTask> findByPlayerIdAndTaskIdIn(long playerId, Collection<UUID> taskIds);

  <V extends View<PlayerTask>> List<V> findByStatus(PlayerTaskStatus status, Class<V> viewType);
}
