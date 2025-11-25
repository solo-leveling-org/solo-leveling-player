package com.sleepkqq.sololeveling.player.model.entity.user;

import com.sleepkqq.sololeveling.player.model.entity.user.dto.LeaderboardUserView;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.TypedTuple;

@TypedTuple
@Getter
@RequiredArgsConstructor
public class LeaderboardUser {

  private final LeaderboardUserView user;
  private final Number score;
  private final Integer position;
}
