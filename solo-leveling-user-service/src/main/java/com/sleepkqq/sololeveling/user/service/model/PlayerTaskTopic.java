package com.sleepkqq.sololeveling.user.service.model;

import com.sleepkqq.sololeveling.proto.player.Assessment;
import com.sleepkqq.sololeveling.proto.player.TaskTopic;
import jakarta.persistence.*;
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
    name = "player_task_topics",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {"player_id", "task_topic"}
        )
    }
)
public class PlayerTaskTopic {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Version
  private int version;

  @Enumerated(EnumType.STRING)
  private TaskTopic taskTopic;

  @Enumerated(EnumType.STRING)
  private Assessment assessment;

  private int completedTasksCount;

  private int skippedTasksCount;

  @CreationTimestamp
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;

  @OneToOne(mappedBy = "playerTaskTopic", cascade = CascadeType.ALL, orphanRemoval = true)
  private Level level;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "player_id", nullable = false)
  private Player player;
}