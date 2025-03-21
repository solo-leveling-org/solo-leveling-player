package com.sleepkqq.sololeveling.user.service.api;

import com.sleepkqq.sololeveling.proto.player.PlayerServiceGrpc.PlayerServiceImplBase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class PlayerApi extends PlayerServiceImplBase {

}
