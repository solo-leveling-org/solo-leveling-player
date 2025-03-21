package com.sleepkqq.sololeveling.user.service.api;


import com.sleepkqq.sololeveling.proto.user.GetCurrentTasksRequest;
import com.sleepkqq.sololeveling.proto.user.GetCurrentTasksResponse;
import com.sleepkqq.sololeveling.proto.user.GetUserInfoRequest;
import com.sleepkqq.sololeveling.proto.user.GetUserInfoResponse;
import com.sleepkqq.sololeveling.proto.user.GetUserTasksRequest;
import com.sleepkqq.sololeveling.proto.user.GetUserTasksResponse;
import com.sleepkqq.sololeveling.proto.user.SaveUserRequest;
import com.sleepkqq.sololeveling.proto.user.SaveUserResponse;
import com.sleepkqq.sololeveling.proto.user.UserServiceGrpc.UserServiceImplBase;
import com.sleepkqq.sololeveling.user.service.mapper.DtoMapper;
import com.sleepkqq.sololeveling.user.service.service.UserService;
import io.grpc.stub.StreamObserver;
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
    var response = GetUserInfoResponse.newBuilder().setSuccess(true);
    try {
      var user = userService.get(request.getId());
      response.setUserInfo(dtoMapper.map(user));
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
      userService.createOrUpdate(dtoMapper.map(userInfo));
    } catch (Exception e) {
      log.error("saveUser error", e);
      response.setSuccess(false);
    }

    responseObserver.onNext(response.build());
    responseObserver.onCompleted();
  }

  @Override
  public void getUserTasks(
      GetUserTasksRequest request,
      StreamObserver<GetUserTasksResponse> responseObserver
  ) {
    var response = GetUserTasksResponse.newBuilder().setSuccess(true);
    try {
      var userTasks = userService.getUserTasks(request.getId());
      response.setUserTasks(dtoMapper.map(userTasks));
    } catch (Exception e) {
      log.error("getUserTasks error", e);
      response.setSuccess(false);
    }

    responseObserver.onNext(response.build());
    responseObserver.onCompleted();
  }

  @Override
  public void getCurrentTasks(
      GetCurrentTasksRequest request,
      StreamObserver<GetCurrentTasksResponse> responseObserver
  ) {
    var response = GetCurrentTasksResponse.newBuilder().setSuccess(true);
    try {
      var currentUserTasks = userService.getCurrentTasks(request.getId());
      response.addAllCurrentTask(dtoMapper.mapCollection(currentUserTasks, dtoMapper::map));
    } catch (Exception e) {
      log.error("getCurrentUserTasks error", e);
      response.setSuccess(false);
    }

    responseObserver.onNext(response.build());
    responseObserver.onCompleted();
  }
}
