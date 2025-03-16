package com.sleepkqq.sololeveling.user.service.model;

import com.sleepkqq.sololeveling.avro.task.TaskTopic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_tasks")
public class UserTasks {

  @Id
  private Long id;

  @Version
  private Integer version;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "jsonb")
  private List<UUID> currentTasks;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "jsonb")
  private List<UUID> completedTasks;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "jsonb")
  private List<UUID> skippedTasks;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "jsonb")
  private List<TaskTopic> taskTopics;

  private int maxTasks;

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId
  @JoinColumn(name = "id")
  private User user;

  public static UserTasks init(User user) {
    return UserTasks.builder()
        .user(user)
        .currentTasks(List.of())
        .completedTasks(List.of())
        .skippedTasks(List.of())
        .taskTopics(List.of())
        .maxTasks(5)
        .build();
  }
}
