package com.sleepkqq.sololeveling.user.service.model;

public enum UserRole {
  USER,
  ADMIN;

  public boolean isUser() {
    return this == USER || isAdmin();
  }

  public boolean isAdmin() {
    return this == ADMIN;
  }

  public com.sleepkqq.sololeveling.user.api.UserRole toApi() {
    return com.sleepkqq.sololeveling.user.api.UserRole.valueOf(name());
  }

  public static UserRole fromApi(com.sleepkqq.sololeveling.user.api.UserRole userRole) {
    return valueOf(userRole.name());
  }
}
