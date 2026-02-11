package com.sleepkqq.sololeveling.player.model.repository.player;

import static com.sleepkqq.sololeveling.player.model.entity.Tables.PLAYER_DAY_ACTIVITY_TABLE;

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerDayActivity;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PlayerDayActivityRepository {

  private final JSqlClient sql;

  public PlayerDayActivity save(PlayerDayActivity activity, SaveMode saveMode) {
    return sql.saveCommand(activity)
        .setMode(saveMode)
        .execute()
        .getModifiedEntity();
  }

  public List<LocalDate> findByMonth(long playerId, YearMonth month) {
    var startDate = month.atDay(1);
    var endDate = month.atEndOfMonth();

    var pda = PLAYER_DAY_ACTIVITY_TABLE;
    return sql.createQuery(pda)
        .where(pda.playerId().eq(playerId))
        .where(pda.dailyTaskCompleted().eq(true))
        .where(pda.day().between(startDate, endDate))
        .select(pda.day())
        .execute();
  }
}
