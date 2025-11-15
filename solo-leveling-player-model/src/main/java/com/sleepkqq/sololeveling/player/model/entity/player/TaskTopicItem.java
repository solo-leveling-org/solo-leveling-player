package com.sleepkqq.sololeveling.player.model.entity.player;

import com.sleepkqq.sololeveling.player.model.entity.task.Task;
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic;
import java.util.UUID;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.GeneratedValue;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.JoinColumn;
import org.babyfish.jimmer.sql.ManyToOne;
import org.babyfish.jimmer.sql.Table;
import org.babyfish.jimmer.sql.meta.UUIDIdGenerator;

@Entity
@Table(name = "task_topic_items")
public interface TaskTopicItem {

  @Id
  @GeneratedValue(generatorType = UUIDIdGenerator.class)
  UUID id();

  TaskTopic topic();

  @ManyToOne
  @JoinColumn(name = "task_id")
  Task task();

  String TOPIC_FIELD = "topic";
}
