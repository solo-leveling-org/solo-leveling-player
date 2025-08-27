package com.sleepkqq.sololeveling.player.model.repository.user;

import static com.sleepkqq.sololeveling.player.model.entity.Tables.USER_TABLE;

import com.sleepkqq.sololeveling.player.model.entity.user.User;
import org.babyfish.jimmer.spring.repository.JRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JRepository<User, Long> {

  default Integer findVersionById(long id) {
    var table = USER_TABLE;
    return sql().createQuery(table)
        .where(table.id().eq(id))
        .select(table.version())
        .fetchFirstOrNull();
  }
}
