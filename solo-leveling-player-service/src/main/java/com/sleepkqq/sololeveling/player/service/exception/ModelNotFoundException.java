package com.sleepkqq.sololeveling.player.service.exception;

import com.sleepkqq.sololeveling.player.service.model.Model;

public class ModelNotFoundException extends RuntimeException {

  public <ID> ModelNotFoundException(Class<? extends Model> clazz, ID id) {
    super(clazz.getSimpleName() + " not found id=" + id);
  }
}
