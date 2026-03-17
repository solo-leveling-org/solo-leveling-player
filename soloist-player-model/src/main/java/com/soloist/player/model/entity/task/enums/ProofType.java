package com.soloist.player.model.entity.task.enums;

import org.babyfish.jimmer.sql.EnumItem;

public enum ProofType {

  @EnumItem(ordinal = 0)
  TEXT,

  @EnumItem(ordinal = 1)
  PHOTO,

  @EnumItem(ordinal = 2)
  VIDEO
}
