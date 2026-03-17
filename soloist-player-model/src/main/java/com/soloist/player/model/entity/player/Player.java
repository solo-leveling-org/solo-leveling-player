package com.soloist.player.model.entity.player;

import com.soloist.player.model.entity.Model;
import com.soloist.player.model.entity.balance.Balance;
import com.soloist.player.model.entity.task.Task;
import com.soloist.player.model.entity.user.User;
import java.util.List;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.JoinColumn;
import org.babyfish.jimmer.sql.OneToMany;
import org.babyfish.jimmer.sql.OneToOne;
import org.jetbrains.annotations.Nullable;

@Entity
public interface Player extends Model {

  @Id
  long id();

  @OneToOne
  @JoinColumn(name = "user_id")
  User user();

  @Nullable
  @OneToOne(mappedBy = "player")
  Balance balance();

  @Nullable
  @OneToOne(mappedBy = "player")
  Stamina stamina();

  @Nullable
  @OneToOne(mappedBy = "player")
  DayStreak dayStreak();

  @OneToMany(mappedBy = "player")
  List<DayActivity> dayActivities();

  @OneToMany(mappedBy = "player")
  List<Task> tasks();
}
