package com.sleepkqq.sololeveling.player.service.model.player;

import static com.sleepkqq.sololeveling.player.service.model.player.enums.PlayerTaskStatus.COMPLETED;
import static com.sleepkqq.sololeveling.player.service.model.player.enums.PlayerTaskStatus.IN_PROGRESS;
import static com.sleepkqq.sololeveling.player.service.model.player.enums.PlayerTaskStatus.PENDING_COMPLETION;
import static com.sleepkqq.sololeveling.player.service.model.player.enums.PlayerTaskStatus.PREPARING;
import static com.sleepkqq.sololeveling.player.service.model.player.enums.PlayerTaskStatus.SKIPPED;
import static java.lang.String.format;

import com.sleepkqq.sololeveling.player.service.model.Model;
import com.sleepkqq.sololeveling.player.service.model.player.enums.PlayerTaskStatus;
import com.sleepkqq.sololeveling.player.service.model.task.Task;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Table(
    name = "player_task",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"player_id", "task_id"})
    }
)
public class PlayerTask implements Model<UUID> {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private PlayerTaskStatus status;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "player_id", nullable = false)
  private Player player;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "task_id", nullable = false)
  private Task task;

  @Version
  private int version;

  @CreationTimestamp
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;

  private LocalDateTime closedAt;

  public void inProgress() {
    if (!PREPARING.equals(status)) {
      throw illegalStatus(IN_PROGRESS);
    }
    status = IN_PROGRESS;
  }

  public void pendingCompletion() {
    if (!IN_PROGRESS.equals(status)) {
      throw illegalStatus(PENDING_COMPLETION);
    }
    status = PENDING_COMPLETION;
    closedAt = LocalDateTime.now();
  }

  public void complete() {
    if (!PENDING_COMPLETION.equals(status)) {
      throw illegalStatus(COMPLETED);
    }
    status = COMPLETED;
  }

  public void skip() {
    if (!IN_PROGRESS.equals(status)) {
      throw illegalStatus(SKIPPED);
    }
    status = SKIPPED;
    closedAt = LocalDateTime.now();
  }

  private IllegalArgumentException illegalStatus(PlayerTaskStatus operation) {
    return new IllegalArgumentException(
        format("Unexpected status=%s for operation=%s", status.name(), operation.name())
    );
  }
}