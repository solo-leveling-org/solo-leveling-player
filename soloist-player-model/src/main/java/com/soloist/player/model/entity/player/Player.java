package com.soloist.player.model.entity.player;

import com.soloist.player.model.entity.Model;
import com.soloist.player.model.entity.user.User;
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

  @Nullable
  @OneToOne(mappedBy = "player")
  PlayerStamina stamina();

  @Nullable
  @OneToOne(mappedBy = "player")
  PlayerDayStreak dayStreak();

  @OneToMany(mappedBy = "player")
  List<PlayerTask> tasks();

  @OneToMany(mappedBy = "player")
  List<PlayerDailyTask> dailyTasks();

  @OneToMany(mappedBy = "player")
  List<PlayerTaskTopic> taskTopics();

  @OneToMany(mappedBy = "player")
  List<PlayerGearItem> gearItems();

  @OneToMany(mappedBy = "player")
  List<PlayerDayActivity> dayActivities();
}
