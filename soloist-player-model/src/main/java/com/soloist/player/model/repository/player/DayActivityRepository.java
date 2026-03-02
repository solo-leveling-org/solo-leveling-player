package com.soloist.player.model.repository.player;

import static com.soloist.player.model.entity.Tables.DAY_ACTIVITY_TABLE;

import com.soloist.player.model.entity.player.DayActivity;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DayActivityRepository {

  private final JSqlClient sql;

  public DayActivity save(DayActivity activity, SaveMode saveMode) {
    return sql.saveCommand(activity)
        .setMode(saveMode)
        .execute()
        .getModifiedEntity();
  }

  public List<LocalDate> findByMonth(long playerId, YearMonth month) {
    var startDate = month.atDay(1);
    var endDate = month.atEndOfMonth();

    var pda = DAY_ACTIVITY_TABLE;
    return sql.createQuery(pda)
        .where(pda.playerId().eq(playerId))
        .where(pda.dailyTaskCompleted().eq(true))
        .where(pda.day().between(startDate, endDate))
        .select(pda.day())
        .execute();
  }
}
