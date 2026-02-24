package com.soloist.player.model.repository.player;

import static com.soloist.player.model.entity.Tables.PLAYER_DAY_STREAK_TABLE;

import com.soloist.player.model.entity.player.PlayerDayStreak;
import java.time.LocalDate;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PlayerDayStreakRepository {

  private final JSqlClient sql;

  @Nullable
  public PlayerDayStreak findNullable(long playerId) {
    var pds = PLAYER_DAY_STREAK_TABLE;
    return sql.createQuery(pds)
        .where(pds.playerId().eq(playerId))
        .select(pds)
        .fetchFirstOrNull();
  }

  public PlayerDayStreak save(PlayerDayStreak dayStreak, SaveMode saveMode) {
    return sql.saveCommand(dayStreak)
        .setMode(saveMode)
        .execute()
        .getModifiedEntity();
  }

  public long resetExpiredStreaks() {
    var pds = PLAYER_DAY_STREAK_TABLE;

    var startOfYesterday = LocalDate.now(ZoneOffset.UTC)
        .minusDays(1)
        .atStartOfDay(ZoneOffset.UTC)
        .toInstant();

    return sql.createUpdate(pds)
        .where(pds.current().gt(0))
        .where(pds.updatedAt().lt(startOfYesterday))
        .set(pds.current(), 0)
        .execute();
  }
}
