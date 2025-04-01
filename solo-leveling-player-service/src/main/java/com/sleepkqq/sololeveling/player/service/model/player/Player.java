package com.sleepkqq.sololeveling.player.service.model.player;

import com.sleepkqq.sololeveling.player.service.model.Model;
import com.sleepkqq.sololeveling.player.service.model.user.User;
import java.util.List;
import org.babyfish.jimmer.sql.Column;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.JoinColumn;
import org.babyfish.jimmer.sql.OneToMany;
import org.babyfish.jimmer.sql.OneToOne;
import org.babyfish.jimmer.sql.Table;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Entity
@Table(name = "players")
public interface Player extends Model {

  @Id
  long id();

  @Column(name = "max_tasks")
  int maxTasks();

  @NotNull
  @OneToOne
  @JoinColumn(name = "user_id")
  User user();

  @Nullable
  @OneToOne(mappedBy = "player")
  Level level();

  @OneToMany(mappedBy = "player")
  List<PlayerTaskTopic> taskTopics();
}