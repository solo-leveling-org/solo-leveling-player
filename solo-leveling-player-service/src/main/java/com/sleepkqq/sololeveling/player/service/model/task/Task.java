package com.sleepkqq.sololeveling.player.service.model.task;

import com.sleepkqq.sololeveling.player.service.model.Model;
import com.sleepkqq.sololeveling.player.service.model.player.PlayerTask;
import com.sleepkqq.sololeveling.player.service.model.task.enums.TaskRarity;
import com.sleepkqq.sololeveling.player.service.model.task.enums.TaskTopic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import java.util.List;
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
@Table(name = "tasks")
public class Task implements Model<UUID> {

  @Id
  private UUID id;

  private String title;

  private String description;

  private int experience;

  private TaskRarity rarity;

  private int agility;

  private int strength;

  private int intelligence;

  @ElementCollection(targetClass = TaskTopic.class, fetch = FetchType.EAGER)
  @CollectionTable(name = "task_topics", joinColumns = @JoinColumn(name = "task_id"))
  @Column(name = "topic")
  private List<TaskTopic> topics;

  @Version
  private int version;

  @CreationTimestamp
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;

  @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<PlayerTask> playerTasks;
}
