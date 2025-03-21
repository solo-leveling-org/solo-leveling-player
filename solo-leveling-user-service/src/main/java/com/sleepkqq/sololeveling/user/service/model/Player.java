package com.sleepkqq.sololeveling.user.service.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "players")
public class Player {

  @Id
  private Long id;

  @Version
  private int version;

  private int maxTasks;

  @CreationTimestamp
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;

  @OneToOne(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
  private Level level;

  @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<PlayerTask> tasks;

  @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<PlayerTaskTopic> taskTopics;

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId
  @JoinColumn(name = "id")
  private User user;

  public static Player init(User user) {
    return Player.builder()
        .user(user)
        .tasks(List.of())
        .taskTopics(List.of())
        .maxTasks(5)
        .build();
  }
}