package com.soloist.player.model.repository.gacha;

import static com.soloist.player.model.entity.Tables.GACHA_MACHINE_TABLE;

import com.soloist.player.model.entity.gacha.GachaMachine;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.View;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GachaMachineRepository {

  private final JSqlClient sql;

  @Nullable
  public <V extends View<GachaMachine>> V findView(UUID id, Class<V> viewType) {
    var table = GACHA_MACHINE_TABLE;
    return sql.createQuery(table)
        .where(table.id().eq(id))
        .select(table.fetch(viewType))
        .fetchFirstOrNull();
  }

  public <V extends View<GachaMachine>> List<V> findAllActiveView(Class<V> viewType) {
    var table = GACHA_MACHINE_TABLE;
    return sql.createQuery(table)
        .where(table.active().eq(true))
        .select(table.fetch(viewType))
        .execute();
  }

  public boolean exists(UUID id) {
    var table = GACHA_MACHINE_TABLE;
    return sql.createQuery(table)
        .where(table.id().eq(id))
        .exists();
  }

  public GachaMachine save(GachaMachine gachaMachine, SaveMode saveMode) {
    return sql.saveCommand(gachaMachine)
        .setMode(saveMode)
        .execute()
        .getModifiedEntity();
  }
}
