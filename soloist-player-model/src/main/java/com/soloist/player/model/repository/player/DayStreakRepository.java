package com.soloist.player.model.repository.player;

import static com.soloist.player.model.entity.Tables.DAY_STREAK_TABLE;

import com.soloist.player.model.entity.player.DayStreak;
import java.time.LocalDate;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.View;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DayStreakRepository {

  private final JSqlClient sql;

  @Nullable
  public <V extends View<DayStreak>> V findView(long playerId, Class<V> viewType) {
    var p = DAY_STREAK_TABLE;
    return sql.createQuery(p)
        .where(p.playerId().eq(playerId))
        .select(p.fetch(viewType))
        .fetchFirstOrNull();
  }

  public DayStreak save(DayStreak dayStreak, SaveMode saveMode) {
    return sql.saveCommand(dayStreak)
        .setMode(saveMode)
        .execute()
        .getModifiedEntity();
  }

  public long resetExpiredStreaks() {
    var pds = DAY_STREAK_TABLE;

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
