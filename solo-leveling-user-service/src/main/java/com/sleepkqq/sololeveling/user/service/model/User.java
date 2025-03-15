package com.sleepkqq.sololeveling.user.service.model;

import com.slepkqq.sololeveling.user.dto.UserRole;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Locale;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

  @Id
  private Long id;

  private String username;

  private String firstName;

  private String lastName;

  private String photoUrl;

  private Locale locale;

  @ElementCollection(targetClass = UserRole.class, fetch = FetchType.LAZY)
  @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
  @Enumerated(EnumType.STRING)
  @Column(name = "role")
  private Collection<UserRole> roles;

  @CreationTimestamp
  @Column(updatable = false)
  private LocalDateTime createdAt;

  private LocalDateTime lastLoginAt;
}
