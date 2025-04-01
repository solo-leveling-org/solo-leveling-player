package com.sleepkqq.sololeveling.player.service.api.user;

import com.sleepkqq.sololeveling.player.service.mapper.ProtoMapper;
import com.sleepkqq.sololeveling.proto.user.AuthUserRequest;
import com.sleepkqq.sololeveling.proto.user.AuthUserResponse;
import com.sleepkqq.sololeveling.proto.user.GetUserInfoRequest;
import com.sleepkqq.sololeveling.proto.user.GetUserInfoResponse;
import com.sleepkqq.sololeveling.proto.user.UserServiceGrpc.UserServiceImplBase;
import com.sleepkqq.sololeveling.player.service.service.user.UserService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class UserApi extends UserServiceImplBase {

  private final UserService userService;
  private final ProtoMapper protoMapper;

  @Override
  public void getUserInfo(
      GetUserInfoRequest request,
      StreamObserver<GetUserInfoResponse> responseObserver
  ) {
    var response = GetUserInfoResponse.newBuilder().setSuccess(true);
    try {
      var user = userService.get(request.getUserId());
      response.setUserInfo(protoMapper.map(user));
    } catch (Exception e) {
      log.error("getUserInfo error", e);
      response.setSuccess(false);
    }

    responseObserver.onNext(response.build());
    responseObserver.onCompleted();
  }

  @Override
  public void authUser(AuthUserRequest request, StreamObserver<AuthUserResponse> responseObserver) {
    var userInfo = request.getUserInfo();
    var response = AuthUserResponse.newBuilder().setSuccess(true);
    try {
      userService.upsert(protoMapper.map(userInfo));
    } catch (Exception e) {
      log.error("authUser error", e);
      response.setSuccess(false);
    }

    responseObserver.onNext(response.build());
    responseObserver.onCompleted();
  }
}
