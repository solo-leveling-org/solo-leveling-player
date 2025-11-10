package com.sleepkqq.sololeveling.player.model.entity.player;

import com.sleepkqq.sololeveling.player.model.entity.Model;
import com.sleepkqq.sololeveling.player.model.entity.user.User;
import java.util.List;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.JoinColumn;
import org.babyfish.jimmer.sql.OneToOne;
import org.babyfish.jimmer.sql.OneToMany;
import org.babyfish.jimmer.sql.Table;
import org.jetbrains.annotations.Nullable;

@Entity
@Table(name = "players")
public interface Player extends Model {

  @Id
  long id();

  int maxTasks();

  int agility();

  int strength();

  int intelligence();

  @OneToOne
  @JoinColumn(name = "user_id")
  User user();

  @Nullable
  @OneToOne(mappedBy = "player")
  Level level();

  @Nullable
  @OneToOne(mappedBy = "player")
  PlayerBalance balance();

  @OneToMany(mappedBy = "player")
  List<PlayerTaskTopic> taskTopics();

  @OneToMany(mappedBy = "player")
  List<PlayerGearItem> gearItems();
}
