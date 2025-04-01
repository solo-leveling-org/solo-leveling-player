package com.sleepkqq.sololeveling.player.service.repository.user;

import static com.sleepkqq.sololeveling.player.service.model.Tables.USER_TABLE;

import com.sleepkqq.sololeveling.player.service.model.user.User;
import java.util.Optional;
import org.babyfish.jimmer.spring.repository.JRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JRepository<User, Long> {

  @Transactional
  default Optional<Integer> findVersionById(long id) {
    var table = USER_TABLE;
    return sql().createQuery(table)
        .where(table.id().eq(id))
        .select(table.version())
        .fetchOptional();
  }
}
