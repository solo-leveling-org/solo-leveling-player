package com.sleepkqq.sololeveling.player.service.api;

import com.sleepkqq.sololeveling.proto.player.GetCurrentTasksRequest;
import com.sleepkqq.sololeveling.proto.player.GetCurrentTasksResponse;
import com.sleepkqq.sololeveling.proto.player.GetPlayerInfoRequest;
import com.sleepkqq.sololeveling.proto.player.GetPlayerInfoResponse;
import com.sleepkqq.sololeveling.proto.player.PlayerServiceGrpc.PlayerServiceImplBase;
import com.sleepkqq.sololeveling.player.service.mapper.DtoMapper;
import com.sleepkqq.sololeveling.player.service.service.PlayerService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class PlayerApi extends PlayerServiceImplBase {

  private final PlayerService playerService;
  private final DtoMapper dtoMapper;

  @Override
  public void getPlayerInfo(
      GetPlayerInfoRequest request,
      StreamObserver<GetPlayerInfoResponse> responseObserver
  ) {
    var response = GetPlayerInfoResponse.newBuilder().setSuccess(true);
    try {
      var playerInfo = playerService.get(request.getPlayerId());
      response.setPlayerInfo(dtoMapper.map(playerInfo));
    } catch (Exception e) {
      log.error("getPlayerInfo error", e);
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
      var currentTasks = playerService.getCurrentTasks(request.getPlayerId());
      response.addAllCurrentTask(dtoMapper.mapCollection(currentTasks, dtoMapper::map));
    } catch (Exception e) {
      log.error("getCurrentTasks error", e);
      response.setSuccess(false);
    }

    responseObserver.onNext(response.build());
    responseObserver.onCompleted();
  }
}
