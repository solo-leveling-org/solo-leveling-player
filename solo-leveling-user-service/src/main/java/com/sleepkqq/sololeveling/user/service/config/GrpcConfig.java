package com.sleepkqq.sololeveling.user.service.config;

import io.envoyproxy.pgv.ValidatorImpl;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;

@Configurable
public class GrpcConfig {

  @Bean
  ValidatorImpl validator() {

  }

}
