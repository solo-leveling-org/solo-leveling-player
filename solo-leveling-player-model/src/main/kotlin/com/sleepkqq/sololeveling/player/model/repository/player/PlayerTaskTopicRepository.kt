package com.sleepkqq.sololeveling.player.model.repository.player

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTaskTopic
import org.babyfish.jimmer.spring.repository.KRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PlayerTaskTopicRepository : KRepository<PlayerTaskTopic, UUID>
