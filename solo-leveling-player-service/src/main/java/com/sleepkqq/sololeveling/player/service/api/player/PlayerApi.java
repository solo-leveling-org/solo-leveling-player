package com.sleepkqq.sololeveling.player.service.api.player;

import com.sleepkqq.sololeveling.player.service.kafka.producer.GenerateTasksProducer;
import com.sleepkqq.sololeveling.player.service.mapper.ProtoMapper;
import com.sleepkqq.sololeveling.player.service.service.player.PlayerTaskService;
import com.sleepkqq.sololeveling.player.service.service.player.PlayerTaskTopicService;
import com.sleepkqq.sololeveling.proto.player.GenerateTasksRequest;
import com.sleepkqq.sololeveling.proto.player.GenerateTasksResponse;
import com.sleepkqq.sololeveling.proto.player.GetCurrentTasksRequest;
import com.sleepkqq.sololeveling.proto.player.GetCurrentTasksResponse;
import com.sleepkqq.sololeveling.proto.player.GetPlayerInfoRequest;
import com.sleepkqq.sololeveling.proto.player.GetPlayerInfoResponse;
import com.sleepkqq.sololeveling.proto.player.PlayerServiceGrpc.PlayerServiceImplBase;
import com.sleepkqq.sololeveling.player.service.service.player.PlayerService;
import com.sleepkqq.sololeveling.proto.player.SavePlayerTopicsRequest;
import com.sleepkqq.sololeveling.proto.player.SavePlayerTopicsResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import one.util.streamex.StreamEx;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class PlayerApi extends PlayerServiceImplBase {

  private final PlayerService playerService;
  private final PlayerTaskService playerTaskService;
  private final PlayerTaskTopicService playerTaskTopicService;
  private final GenerateTasksProducer generateTasksProducer;
  private final ProtoMapper protoMapper;

  @Override
  public void getPlayerInfo(
      GetPlayerInfoRequest request,
      StreamObserver<GetPlayerInfoResponse> responseObserver
  ) {
    var response = GetPlayerInfoResponse.newBuilder().setSuccess(true);
    try {
      var player = playerService.get(request.getPlayerId());
      response.setPlayerInfo(protoMapper.map(player));
    } catch (Exception e) {
      log.error("getPlayerInfo error", e);
      response.setSuccess(false);
    }

    responseObserver.onNext(response.build());
    responseObserver.onCompleted();
  }

  @Override
  @Transactional
  public void getCurrentTasks(
      GetCurrentTasksRequest request,
      StreamObserver<GetCurrentTasksResponse> responseObserver
  ) {
    var response = GetCurrentTasksResponse.newBuilder().setSuccess(true);
    try {
      var currentTasks = playerTaskService.getCurrentTasks(request.getPlayerId());
      response.addAllCurrentTask(protoMapper.mapCollection(currentTasks, protoMapper::map));
    } catch (Exception e) {
      log.error("getCurrentTasks error", e);
      response.setSuccess(false);
    }

    responseObserver.onNext(response.build());
    responseObserver.onCompleted();
  }

  @Override
  public void savePlayerTopics(
      SavePlayerTopicsRequest request,
      StreamObserver<SavePlayerTopicsResponse> responseObserver
  ) {
    var response = SavePlayerTopicsResponse.newBuilder().setSuccess(true);
    try {
      StreamEx.of(protoMapper.mapCollection(request.getTopicList(), protoMapper::map))
          .map(t -> playerTaskTopicService.initialize(request.getPlayerId(), t))
          .forEach(playerTaskTopicService::save);
    } catch (Exception e) {
      log.error("savePlayerTopics error", e);
      response.setSuccess(false);
    }

    responseObserver.onNext(response.build());
    responseObserver.onCompleted();
  }

  @Override
  public void generateTasks(
      GenerateTasksRequest request,
      StreamObserver<GenerateTasksResponse> responseObserver
  ) {
    var response = GenerateTasksResponse.newBuilder().setSuccess(true);
    try {
      generateTasksProducer.send(request.getPlayerId());
    } catch (Exception e) {
      log.error("savePlayerTopics error", e);
      response.setSuccess(false);
    }

    responseObserver.onNext(response.build());
    responseObserver.onCompleted();
  }
}
