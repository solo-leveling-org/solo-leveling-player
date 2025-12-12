package com.sleepkqq.sololeveling.player.model.entity.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.TypedTuple;

@TypedTuple
@Getter
@RequiredArgsConstructor
public class UsersStats {

  private final long total;
  private final long returning;

  private final long todayTotal;
  private final long todayReturning;
  private final long todayNew;

  private final long weekTotal;
  private final long weekReturning;
  private final long weekNew;

  private final long monthTotal;
  private final long monthReturning;
  private final long monthNew;
}
