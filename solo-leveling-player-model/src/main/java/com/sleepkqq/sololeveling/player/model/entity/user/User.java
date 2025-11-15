package com.sleepkqq.sololeveling.player.model.entity.user;

import com.sleepkqq.sololeveling.player.model.entity.Model;
import com.sleepkqq.sololeveling.player.model.entity.player.Player;
import java.time.LocalDateTime;
import java.util.List;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.OneToMany;
import org.babyfish.jimmer.sql.OneToOne;
import org.babyfish.jimmer.sql.Table;
import org.jetbrains.annotations.Nullable;

@Entity
@Table(name = "users")
public interface User extends Model {

  @Id
  long id();

  String username();

  String firstName();

  String lastName();

  String photoUrl();

  String locale();

  @Nullable
  String manualLocale();

  LocalDateTime lastLoginAt();

  @OneToMany(mappedBy = "user")
  List<UserRoleItem> roles();

  @Nullable
  @OneToOne(mappedBy = "user")
  Player player();
}
