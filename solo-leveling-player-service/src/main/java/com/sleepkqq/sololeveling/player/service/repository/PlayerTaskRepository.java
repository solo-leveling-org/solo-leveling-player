package com.sleepkqq.sololeveling.player.service.repository;

import com.sleepkqq.sololeveling.proto.player.PlayerTaskStatus;
import com.sleepkqq.sololeveling.player.service.model.PlayerTask;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerTaskRepository extends JpaRepository<PlayerTask, UUID> {

  List<PlayerTask> findByPlayerIdAndStatus(Long playerId, PlayerTaskStatus status);

  List<PlayerTask> findByPlayerIdAndStatusIn(
      Long playerId,
      Collection<PlayerTaskStatus> statuses
  );

  Optional<PlayerTask> findByPlayerIdAndTaskId(Long playerId, UUID taskId);
}