package com.sleepkqq.sololeveling.user.service.model;

import com.sleepkqq.sololeveling.proto.player.TaskRarity;
import com.sleepkqq.sololeveling.proto.player.TaskTopic;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
public class Task {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Version
  private int version;

  private String title;

  private String description;

  private int experience;

  @Enumerated(EnumType.STRING)
  private TaskRarity rarity;

  private int agility;

  private int strength;

  private int intelligence;

  @CreationTimestamp
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;

  @ElementCollection(targetClass = TaskTopic.class, fetch = FetchType.EAGER)
  @CollectionTable(name = "task_topics", joinColumns = @JoinColumn(name = "task_id"))
  @Enumerated(EnumType.STRING)
  @Column(name = "topic")
  private List<TaskTopic> topics;
}
