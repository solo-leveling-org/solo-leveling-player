package com.sleepkqq.sololeveling.player.service.service.task;

import com.sleepkqq.sololeveling.player.service.model.task.enums.TaskTopic;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import one.util.streamex.StreamEx;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class DefineTaskTopicService {

  private static final Random RANDOM = new Random();

  public List<TaskTopic> define(Set<TaskTopic> topics) {
    if (CollectionUtils.isEmpty(topics)) {
      return List.of();
    }

    var topicsList = new ArrayList<>(topics);
    Collections.shuffle(topicsList);
    if (oneTopic()) {
      return getFirstTopic(topicsList);
    }

    return StreamEx.of(topics)
        .mapPartial(t -> StreamEx.of(t.getCompatibleTopics())
            .findFirst(topics::contains)
            .map(c -> List.of(t, c))
        )
        .findFirst()
        .orElseGet(() -> getFirstTopic(topicsList));
  }

  private List<TaskTopic> getFirstTopic(List<TaskTopic> topicsList) {
    return List.of(topicsList.getFirst());
  }

  private boolean oneTopic() {
    return RANDOM.nextInt(3) < 2;
  }
}
