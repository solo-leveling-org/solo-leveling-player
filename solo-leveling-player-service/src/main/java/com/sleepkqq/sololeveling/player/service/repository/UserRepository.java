package com.sleepkqq.sololeveling.player.service.repository;

import com.sleepkqq.sololeveling.player.service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
