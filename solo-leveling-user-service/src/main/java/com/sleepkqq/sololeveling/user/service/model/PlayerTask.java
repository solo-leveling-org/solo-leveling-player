package com.sleepkqq.sololeveling.user.service.model;

import com.sleepkqq.sololeveling.proto.player.PlayerTaskStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "player_task")
public class PlayerTask {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Version
  private int version;

  @Enumerated(EnumType.STRING)
  private PlayerTaskStatus status;

  @CreationTimestamp
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;

  private LocalDateTime closedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "player_id", nullable = false)
  private Player player;

  public void pendingCompletion() {
    status = PlayerTaskStatus.PENDING_COMPLETION;
  }

  public void complete() {
    status = PlayerTaskStatus.COMPLETED;
    closedAt = LocalDateTime.now();
  }

  public void skip() {
    status = PlayerTaskStatus.SKIPPED;
    closedAt = LocalDateTime.now();
  }
}