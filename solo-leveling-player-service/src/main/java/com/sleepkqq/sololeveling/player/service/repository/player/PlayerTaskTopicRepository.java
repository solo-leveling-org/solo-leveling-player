package com.sleepkqq.sololeveling.player.service.repository.player;

import com.sleepkqq.sololeveling.player.service.model.player.PlayerTaskTopic;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerTaskTopicRepository extends JpaRepository<PlayerTaskTopic, UUID> {

}
