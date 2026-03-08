package com.soloist.player.model.entity.user;

import com.soloist.player.model.entity.user.enums.UserRole;
import java.util.UUID;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.GeneratedValue;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.JoinColumn;
import org.babyfish.jimmer.sql.ManyToOne;
import org.babyfish.jimmer.sql.meta.UUIDIdGenerator;

@Entity
public interface UserRoleItem {

  @Id
  @GeneratedValue(generatorType = UUIDIdGenerator.class)
  UUID id();

  UserRole role();

  @ManyToOne
  @JoinColumn(name = "user_id")
  User user();
}
