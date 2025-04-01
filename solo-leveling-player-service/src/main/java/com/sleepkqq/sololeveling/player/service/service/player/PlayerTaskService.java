package com.sleepkqq.sololeveling.player.service.service.player;

import static com.sleepkqq.sololeveling.player.service.model.player.enums.PlayerTaskStatus.IN_PROGRESS;
import static com.sleepkqq.sololeveling.player.service.model.player.enums.PlayerTaskStatus.PENDING_COMPLETION;
import static com.sleepkqq.sololeveling.player.service.model.player.enums.PlayerTaskStatus.PREPARING;
import static java.lang.String.format;

import com.sleepkqq.sololeveling.player.service.model.player.PlayerTask;
import com.sleepkqq.sololeveling.player.service.model.player.PlayerTaskDraft;
import com.sleepkqq.sololeveling.player.service.model.player.enums.PlayerTaskStatus;
import com.sleepkqq.sololeveling.player.service.repository.player.PlayerTaskRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlayerTaskService {

  private static final Set<PlayerTaskStatus> CURRENT_TASKS_STATUSES = Set.of(
      PREPARING, IN_PROGRESS, PENDING_COMPLETION
  );

  private final PlayerTaskRepository playerTaskRepository;

  @Transactional
  public UUID getTaskId(long playerId, UUID taskId) {
    return playerTaskRepository.findIdByPlayerIdAndTaskId(playerId, taskId)
        .orElseThrow(() -> new IllegalArgumentException(
            format("PlayerTask not found playerId=%d taskId=%s", playerId, taskId)
        ));
  }

  @Transactional
  public PlayerTask create(PlayerTask playerTask) {
    return playerTaskRepository.save(playerTask);
  }

  @Transactional
  public PlayerTask update(PlayerTask playerTask, LocalDateTime now) {
    return playerTaskRepository.update(PlayerTaskDraft.$.produce(playerTask, p ->
        p.setUpdatedAt(now)
    ));
  }

  @Transactional
  public PlayerTask update(PlayerTask playerTask) {
    return update(playerTask, LocalDateTime.now());
  }

  @Transactional
  public void setStatus(UUID id, PlayerTaskStatus status, LocalDateTime now) {
    playerTaskRepository.setStatus(id, status, now);
  }

  @Transactional
  public void setStatus(UUID id, PlayerTaskStatus status) {
    setStatus(id, status, LocalDateTime.now());
  }

  @Transactional
  public List<PlayerTask> getCurrentTasks(long playerId) {
    return playerTaskRepository.findByPlayerIdAndStatusIn(playerId, CURRENT_TASKS_STATUSES);
  }

  @Transactional
  public long getCurrentTasksCount(long playerId) {
    return playerTaskRepository.countByPlayerIdAndStatusIn(playerId, CURRENT_TASKS_STATUSES);
  }
}
