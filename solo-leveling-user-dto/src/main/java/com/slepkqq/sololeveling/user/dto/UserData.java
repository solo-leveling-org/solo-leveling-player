package com.slepkqq.sololeveling.user.dto;

import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@RequiredArgsConstructor
public final class UserData implements UserDetails {

  private final Long id;

  private final String username;

  private final String firstName;

  private final String lastName;

  private final String photoUrl;

  private final Locale locale;

  private final Set<UserRole> roles;

  @Override
  public Collection<UserRole> getAuthorities() {
    return roles;
  }

  @Override
  public String getPassword() {
    return null;
  }

  public static UserData fromTgUser(TgUserData tgUser) {
    return new UserData(
        tgUser.id(),
        tgUser.username(),
        tgUser.firstName(),
        tgUser.lastName(),
        tgUser.photoUrl(),
        Optional.of(tgUser.languageCode())
            .filter("ru"::equalsIgnoreCase)
            .map(Locale::forLanguageTag)
            .orElse(Locale.ENGLISH),
        Set.of(UserRole.USER)
    );
  }
}
