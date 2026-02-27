package com.soloist.player.model.repository.task;

import static com.soloist.player.model.entity.Tables.VECTOR_TASK_TABLE;

import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.JSqlClient;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VectorTaskRepository {

  private final JSqlClient sql;

  public int deleteAll() {
    return sql.createDelete(VECTOR_TASK_TABLE).execute();
  }
}
