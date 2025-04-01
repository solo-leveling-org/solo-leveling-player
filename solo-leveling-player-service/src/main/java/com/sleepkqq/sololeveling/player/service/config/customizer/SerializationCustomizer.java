package com.sleepkqq.sololeveling.player.service.config.customizer;

import static com.sleepkqq.sololeveling.player.service.mapper.EnumOrdinalMapper.createMapper;
import static com.sleepkqq.sololeveling.player.service.model.task.TaskProps.TOPICS;
import static com.sleepkqq.sololeveling.player.service.model.user.UserProps.ROLES;

import com.sleepkqq.sololeveling.player.service.model.task.enums.TaskTopic;
import com.sleepkqq.sololeveling.player.service.model.user.enums.UserRole;
import org.babyfish.jimmer.sql.JSqlClient.Builder;
import org.babyfish.jimmer.sql.runtime.Customizer;
import org.springframework.stereotype.Component;

@Component
public class SerializationCustomizer implements Customizer {

  @Override
  public void customize(Builder builder) {
    builder
        .setSerializedPropObjectMapper(TOPICS, createMapper(TaskTopic.class))
        .setSerializedPropObjectMapper(ROLES, createMapper(UserRole.class));
  }
}
