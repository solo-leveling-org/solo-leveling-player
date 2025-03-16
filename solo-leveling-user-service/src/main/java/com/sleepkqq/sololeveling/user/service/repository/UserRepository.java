package com.sleepkqq.sololeveling.user.service.repository;

import com.sleepkqq.sololeveling.user.service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
