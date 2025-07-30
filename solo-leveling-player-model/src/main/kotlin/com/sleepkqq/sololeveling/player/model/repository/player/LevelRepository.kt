package com.sleepkqq.sololeveling.player.model.repository.player

import com.sleepkqq.sololeveling.player.model.entity.player.Level
import org.babyfish.jimmer.spring.repository.KRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface LevelRepository : KRepository<Level, UUID>
