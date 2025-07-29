package com.sleepkqq.sololeveling.player.service.api.user

import com.google.protobuf.Empty
import com.sleepkqq.sololeveling.player.service.mapper.ProtoMapper
import com.sleepkqq.sololeveling.player.service.service.user.UserService
import com.sleepkqq.sololeveling.proto.user.AuthUserRequest
import com.sleepkqq.sololeveling.proto.user.GetUserInfoRequest
import com.sleepkqq.sololeveling.proto.user.GetUserInfoResponse
import com.sleepkqq.sololeveling.proto.user.UserServiceGrpc.UserServiceImplBase
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import org.springframework.grpc.server.service.GrpcService

@Suppress("unused")
@GrpcService
class UserApi(
	private val userService: UserService,
	private val protoMapper: ProtoMapper
) : UserServiceImplBase() {

	private val log = LoggerFactory.getLogger(javaClass)

	override fun getUserInfo(
		request: GetUserInfoRequest,
		responseObserver: StreamObserver<GetUserInfoResponse>
	) {
		try {
			val user = userService.get(request.userId)
			val response = GetUserInfoResponse.newBuilder()
				.setUser(protoMapper.map(user))
				.build()

			responseObserver.onNext(response)
			responseObserver.onCompleted()
		} catch (e: Exception) {
			log.error("getUserInfo error", e)
			responseObserver.onError(e)
		}
	}

	override fun authUser(
		request: AuthUserRequest,
		responseObserver: StreamObserver<Empty>
	) {
		try {
			val user = protoMapper.map(request.user)
			userService.upsert(user.toEntity())
			val response = Empty.newBuilder().build()

			responseObserver.onNext(response)
			responseObserver.onCompleted()
		} catch (e: Exception) {
			log.error("authUser error", e)
			responseObserver.onError(e)
		}
	}
}
