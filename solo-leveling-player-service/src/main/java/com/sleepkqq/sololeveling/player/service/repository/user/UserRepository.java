package com.sleepkqq.sololeveling.player.service.repository.user;

import com.sleepkqq.sololeveling.player.service.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
