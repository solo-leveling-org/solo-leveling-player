package com.sleepkqq.sololeveling.user.service.model;

import com.sleepkqq.sololeveling.avro.task.TaskTopic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users_tasks")
public class UserTasks {

  @Id
  private UUID id;

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
  private List<TaskTopic> taskTopics;

  private int maxTasks;
}
