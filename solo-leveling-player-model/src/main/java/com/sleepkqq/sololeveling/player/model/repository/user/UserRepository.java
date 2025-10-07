package com.sleepkqq.sololeveling.player.model.repository.user;

import static com.sleepkqq.sololeveling.player.model.entity.Tables.USER_TABLE;

import com.sleepkqq.sololeveling.player.model.entity.user.User;
import com.sleepkqq.sololeveling.player.model.entity.user.UserFetcher;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.View;
import org.babyfish.jimmer.sql.JSqlClient;
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
  public Integer findVersionById(long id) {
    var table = USER_TABLE;
    return sql.createQuery(table)
        .where(table.id().eq(id))
        .select(table.version())
        .fetchFirstOrNull();
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
}
