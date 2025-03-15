package com.sleepkqq.sololeveling.user.service.mapper;

import com.slepkqq.sololeveling.user.dto.UserRole;
import java.util.Collection;
import java.util.function.Function;
import one.util.streamex.StreamEx;
import org.springframework.stereotype.Component;

@Component
public class DtoMapper {

  public UserRole map(com.sleepkqq.sololeveling.proto.user.UserRole proto) {
    return UserRole.valueOf(proto.name());
  }

  public com.sleepkqq.sololeveling.proto.user.UserRole map(UserRole dto) {
    return com.sleepkqq.sololeveling.proto.user.UserRole.valueOf(dto.name());
  }

  public <T, R> Collection<R> mapCollection(Collection<T> collection, Function<T, R> mapper) {
    return StreamEx.of(collection).map(mapper).toList();
  }
}
