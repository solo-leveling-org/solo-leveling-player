package com.sleepkqq.sololeveling.player.model.repository.user;

import static com.sleepkqq.sololeveling.player.model.entity.Tables.PLAYER_TABLE;
import static com.sleepkqq.sololeveling.player.model.entity.Tables.PLAYER_TASK_TABLE;
import static com.sleepkqq.sololeveling.player.model.entity.Tables.USER_TABLE;

import com.sleepkqq.sololeveling.jimmer.predicate.filter.DateFilter.DayRange;
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerTaskStatus;
import com.sleepkqq.sololeveling.player.model.entity.user.LeaderboardUser;
import com.sleepkqq.sololeveling.player.model.entity.user.LeaderboardUserMapper;
import com.sleepkqq.sololeveling.player.model.entity.user.User;
import com.sleepkqq.sololeveling.player.model.entity.user.UserFetcher;
import com.sleepkqq.sololeveling.player.model.entity.user.dto.LeaderboardUserView;
import com.sleepkqq.sololeveling.proto.player.RequestPaging;
import com.sleepkqq.sololeveling.proto.user.LeaderboardType;
import java.math.BigDecimal;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.Page;
import org.babyfish.jimmer.View;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.Expression;
import org.babyfish.jimmer.sql.ast.Predicate;
import org.babyfish.jimmer.sql.ast.Selection;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepository {

  private final JSqlClient sql;

  @Nullable
  public User findNullable(long id, UserFetcher fetcher) {
    var table = USER_TABLE;
    return sql.createQuery(table)
        .where(table.id().eq(id))
        .select(table.fetch(fetcher))
        .fetchFirstOrNull();
  }

  public User save(User user, SaveMode saveMode) {
    return sql.saveCommand(user)
        .setMode(saveMode)
        .execute()
        .getModifiedEntity();
  }

  @Nullable
  public <V extends View<User>> V findView(long id, Class<V> viewType) {
    var table = USER_TABLE;
    return sql.createQuery(table)
        .where(table.id().eq(id))
        .select(table.fetch(viewType))
        .fetchFirstOrNull();
  }

  public void updateLocale(long id, Locale locale) {
    var table = USER_TABLE;
    sql.createUpdate(table)
        .where(table.id().eq(id))
        .set(table.manualLocale(), locale.getLanguage())
        .execute();
  }

  @SuppressWarnings("unchecked")
  public Page<LeaderboardUser> getLeaderboardPage(
      LeaderboardType type,
      DayRange range,
      RequestPaging paging
  ) {
    var p = PLAYER_TABLE;

    Expression<? extends Number> expression;
    Predicate predicate;

    switch (type) {
      case TASKS -> {
        var pt = PLAYER_TASK_TABLE;

        var query = sql.createSubQuery(pt)
            .where(
                pt.player().id().eq(p.id()),
                pt.status().eq(PlayerTaskStatus.COMPLETED)
            );

        if (!range.isEmpty()) {
          query = query.where(
              pt.updatedAt().ge(range.from()),
              pt.updatedAt().lt(range.to())
          );
        }

        var taskCount = query.selectCount();
        expression = taskCount;
        predicate = taskCount.gt(0L);
      }

      case BALANCE -> {
        var balance = p.balance().balance();
        expression = balance;
        predicate = balance.gt(BigDecimal.ZERO);
      }

      case LEVEL -> {
        var level = p.level();
        expression = level.level();
        predicate = level.totalExperience().gt(0);
      }

      default -> throw new IllegalArgumentException("Unknown leaderboard type: " + type);
    }

    var baseUser = sql.createBaseQuery(p)
        .where(predicate)
        .addSelect(p.user())
        .addSelect(expression)
        .addSelect(
            Expression.numeric().sql(
                Long.class,
                "row_number() over(order by %e desc, %e asc)",
                expression,
                p.id()
            )
        )
        .asBaseTable();

    return sql.createQuery(baseUser)
        .select(LeaderboardUserMapper
            .user(baseUser.get_1().fetch(LeaderboardUserView.class))
            .score((Selection<Number>) baseUser.get_2())
            .position(baseUser.get_3())
        )
        .fetchPage(paging.getPage(), paging.getPageSize());
  }
}
