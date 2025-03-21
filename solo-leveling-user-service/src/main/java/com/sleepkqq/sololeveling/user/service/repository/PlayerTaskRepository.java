package com.sleepkqq.sololeveling.user.service.repository;

import com.sleepkqq.sololeveling.proto.player.PlayerTaskStatus;
import com.sleepkqq.sololeveling.user.service.model.PlayerTask;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerTaskRepository extends JpaRepository<PlayerTask, UUID> {

  List<PlayerTask> findByPlayerIdAndStatus(Long playerId, PlayerTaskStatus status);

  List<PlayerTask> findByPlayerIdAndStatusIn(
      Long userTasksId,
      Collection<PlayerTaskStatus> statuses
  );
}