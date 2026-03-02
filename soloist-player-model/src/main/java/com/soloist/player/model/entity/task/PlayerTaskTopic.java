package com.soloist.player.model.entity.task;

import com.soloist.player.model.entity.Model;
import com.soloist.player.model.entity.player.Level;
import com.soloist.player.model.entity.player.Player;
import com.soloist.player.model.entity.task.enums.TaskTopic;
import java.util.UUID;
import org.babyfish.jimmer.sql.Column;
import org.babyfish.jimmer.sql.DissociateAction;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.GeneratedValue;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.JoinColumn;
import org.babyfish.jimmer.sql.ManyToOne;
import org.babyfish.jimmer.sql.OnDissociate;
import org.babyfish.jimmer.sql.OneToOne;
import org.babyfish.jimmer.sql.meta.UUIDIdGenerator;
import org.jetbrains.annotations.Nullable;

@Entity
public interface PlayerTaskTopic extends Model {

  @Id
  @GeneratedValue(generatorType = UUIDIdGenerator.class)
  UUID id();

  TaskTopic taskTopic();

  @Column(name = "is_active")
  boolean active();

  @ManyToOne
  @OnDissociate(DissociateAction.DELETE)
  @JoinColumn(name = "player_id")
  Player player();

  @Nullable
  @OneToOne(mappedBy = "playerTaskTopic")
  Level level();
}
