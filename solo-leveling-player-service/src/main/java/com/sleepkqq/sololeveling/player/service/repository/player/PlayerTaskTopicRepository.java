package com.sleepkqq.sololeveling.player.service.repository.player;

import com.sleepkqq.sololeveling.player.service.model.player.PlayerTaskTopic;
import java.util.UUID;
import org.babyfish.jimmer.spring.repository.JRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerTaskTopicRepository extends JRepository<PlayerTaskTopic, UUID> {

}
