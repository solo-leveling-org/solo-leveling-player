package com.sleepkqq.sololeveling.user.service.api;


import com.sleepkqq.sololeveling.proto.user.GetUserInfoRequest;
import com.sleepkqq.sololeveling.proto.user.GetUserInfoResponse;
import com.sleepkqq.sololeveling.proto.user.SaveUserRequest;
import com.sleepkqq.sololeveling.proto.user.SaveUserResponse;
import com.sleepkqq.sololeveling.proto.user.UserInfo;
import com.sleepkqq.sololeveling.proto.user.UserServiceGrpc.UserServiceImplBase;
import com.sleepkqq.sololeveling.user.service.mapper.DtoMapper;
import com.sleepkqq.sololeveling.user.service.model.User;
import com.sleepkqq.sololeveling.user.service.service.UserService;
import io.grpc.stub.StreamObserver;
import java.time.LocalDateTime;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class UserApi extends UserServiceImplBase {

  private final UserService userService;
  private final DtoMapper dtoMapper;

  @Override
  public void getUserInfo(
      GetUserInfoRequest request,
      StreamObserver<GetUserInfoResponse> responseObserver
  ) {
    var response = GetUserInfoResponse.newBuilder();
    try {
      var user = userService.getById(request.getId());
      response.setSuccess(true)
          .setUserInfo(UserInfo.newBuilder()
              .setId(user.getId())
              .setUsername(user.getUsername())
              .setFirstName(user.getFirstName())
              .setLastName(user.getLastName())
              .setPhotoUrl(user.getPhotoUrl())
              .setLocale(user.getLocale().toLanguageTag())
              .addAllRole(dtoMapper.mapCollection(user.getRoles(), dtoMapper::map))
              .build()
          );
    } catch (Exception e) {
      log.error("getUserInfo error", e);
      response.setSuccess(false);
    }

    responseObserver.onNext(response.build());
    responseObserver.onCompleted();
  }

  @Override
  public void saveUser(SaveUserRequest request, StreamObserver<SaveUserResponse> responseObserver) {
    var userInfo = request.getUserInfo();
    var response = SaveUserResponse.newBuilder().setSuccess(true);
    try {
      userService.save(User.builder()
          .id(userInfo.getId())
          .username(userInfo.getUsername())
          .firstName(userInfo.getFirstName())
          .lastName(userInfo.getLastName())
          .photoUrl(userInfo.getPhotoUrl())
          .locale(Locale.forLanguageTag(userInfo.getLocale()))
          .roles(dtoMapper.mapCollection(userInfo.getRoleList(), dtoMapper::map))
          .lastLoginAt(LocalDateTime.now())
          .build());
    } catch (Exception e) {
      log.error("saveUser error", e);
      response.setSuccess(false);
    }

    responseObserver.onNext(response.build());
    responseObserver.onCompleted();
  }
}
