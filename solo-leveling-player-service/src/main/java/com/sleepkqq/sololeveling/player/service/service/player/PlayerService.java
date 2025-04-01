package com.sleepkqq.sololeveling.player.service.service.player;

import com.sleepkqq.sololeveling.player.service.exception.ModelNotFoundException;
import com.sleepkqq.sololeveling.player.service.model.Immutables;
import com.sleepkqq.sololeveling.player.service.model.player.Player;
import com.sleepkqq.sololeveling.player.service.repository.player.PlayerRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlayerService {

  private final PlayerRepository playerRepository;

  @Transactional
  public Player get(long id) {
    return find(id).orElseThrow(() -> new ModelNotFoundException(Player.class, id));
  }

  @Transactional
  public Player create(Player player) {
    return playerRepository.save(player);
  }

  @Transactional
  public Player update(Player player, LocalDateTime now) {
    return playerRepository.update(Immutables.createPlayer(player, p ->
        p.setUpdatedAt(now)
    ));
  }

  @Transactional
  public Player update(Player player) {
    return update(player, LocalDateTime.now());
  }

  @Transactional
  public Optional<Player> find(long id) {
    return playerRepository.findById(id);
  }
}
