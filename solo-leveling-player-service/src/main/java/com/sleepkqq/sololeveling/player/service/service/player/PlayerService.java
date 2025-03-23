package com.sleepkqq.sololeveling.player.service.service.player;

import static com.sleepkqq.sololeveling.player.service.model.player.enums.PlayerTaskStatus.IN_PROGRESS;
import static com.sleepkqq.sololeveling.player.service.model.player.enums.PlayerTaskStatus.PENDING_COMPLETION;
import static com.sleepkqq.sololeveling.player.service.model.player.enums.PlayerTaskStatus.PREPARING;
import static java.lang.String.format;

import com.sleepkqq.sololeveling.player.service.exception.ModelNotFoundException;
import com.sleepkqq.sololeveling.player.service.model.player.Player;
import com.sleepkqq.sololeveling.player.service.model.player.PlayerTask;
import com.sleepkqq.sololeveling.player.service.repository.player.PlayerRepository;
import com.sleepkqq.sololeveling.player.service.repository.player.PlayerTaskRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlayerService {

  private final PlayerRepository playerRepository;
  private final PlayerTaskRepository playerTaskRepository;

  public Player get(long id) {
    return find(id).orElseThrow(() -> new ModelNotFoundException(Player.class, id));
  }

  public Player save(Player player) {
    return playerRepository.save(player);
  }

  public Optional<Player> find(long id) {
    return playerRepository.findById(id);
  }

  public List<PlayerTask> getCurrentTasks(long id) {
    return playerTaskRepository.findByPlayerIdAndStatusIn(
        id, Set.of(PREPARING, IN_PROGRESS, PENDING_COMPLETION)
    );
  }

  public PlayerTask getTask(long id, UUID taskId) {
    return playerTaskRepository.findByPlayerIdAndTaskId(id, taskId)
        .orElseThrow(() -> new IllegalArgumentException(
            format("PlayerTask not found playerId=%d taskId=%s", id, taskId)
        ));
  }

  public PlayerTask saveTask(PlayerTask playerTask) {
    return playerTaskRepository.save(playerTask);
  }
}
