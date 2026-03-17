package com.soloist.player.model.entity.task;

import com.soloist.player.model.entity.Model;
import com.soloist.player.model.entity.player.Player;
import com.soloist.player.model.entity.task.enums.TaskType;
import com.soloist.player.model.entity.task.enums.ProofType;
import java.time.LocalDate;
import java.util.UUID;
import org.babyfish.jimmer.sql.Column;
import org.babyfish.jimmer.sql.DissociateAction;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.GeneratedValue;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.JoinColumn;
import org.babyfish.jimmer.sql.ManyToOne;
import org.babyfish.jimmer.sql.OnDissociate;
import org.babyfish.jimmer.sql.Table;
import org.babyfish.jimmer.sql.meta.UUIDIdGenerator;
import org.jetbrains.annotations.Nullable;

@Entity
@Table(name = "task")
public interface Task extends Model {

  @Id
  @GeneratedValue(generatorType = UUIDIdGenerator.class)
  UUID id();

  TaskType type();

  @Nullable
  String name();

  int goal();

  int progress();

  int gemReward();

  @Column(name = "is_completed")
  boolean completed();

  ProofType proofType();

  LocalDate day();

  @ManyToOne
  @JoinColumn(name = "player_id")
  @OnDissociate(DissociateAction.DELETE)
  Player player();
}
