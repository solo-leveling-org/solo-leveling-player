package com.sleepkqq.sololeveling.player.model.interceptor;

import com.sleepkqq.sololeveling.player.model.entity.localization.LocalizationItem;
import com.sleepkqq.sololeveling.player.model.entity.localization.LocalizationItemDraft;
import com.sleepkqq.sololeveling.player.model.entity.localization.LocalizationItemProps;
import java.util.UUID;
import org.babyfish.jimmer.ImmutableObjects;
import org.babyfish.jimmer.sql.DraftInterceptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component
public class LocalizationItemInterceptor
    implements DraftInterceptor<LocalizationItem, LocalizationItemDraft> {

  @Override
  public void beforeSave(
      @NotNull LocalizationItemDraft draft,
      @Nullable LocalizationItem original
  ) {
    if (original == null) {
      if (!ImmutableObjects.isLoaded(draft, LocalizationItemProps.ID)) {
        draft.setId(UUID.randomUUID());
      }
    }
  }
}
