package com.sleepkqq.sololeveling.player.service.model.user;

import com.sleepkqq.sololeveling.player.service.model.Model;
import com.sleepkqq.sololeveling.player.service.model.player.Player;
import com.sleepkqq.sololeveling.player.service.model.user.enums.UserRole;
import java.time.LocalDateTime;
import java.util.List;
import org.babyfish.jimmer.sql.Column;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.OneToOne;
import org.babyfish.jimmer.sql.Serialized;
import org.babyfish.jimmer.sql.Table;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Entity
@Table(name = "users")
public interface User extends Model {

  @Id
  long id();

  @NotNull
  @Column(name = "username")
  String username();

  @NotNull
  @Column(name = "first_name")
  String firstName();

  @NotNull
  @Column(name = "last_name")
  String lastName();

  @NotNull
  @Column(name = "photo_url")
  String photoUrl();

  @NotNull
  @Column(name = "locale")
  String locale();

  @NotNull
  @Column(name = "last_login_at")
  LocalDateTime lastLoginAt();

  @Serialized
  @Column(name = "roles")
  List<UserRole> roles();

  @Nullable
  @OneToOne(mappedBy = "user")
  Player player();
}
