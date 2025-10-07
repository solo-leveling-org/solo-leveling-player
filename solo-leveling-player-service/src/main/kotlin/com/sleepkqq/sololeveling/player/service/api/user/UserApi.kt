package com.sleepkqq.sololeveling.player.service.api.user

import com.google.protobuf.Empty
import com.sleepkqq.sololeveling.player.model.entity.Fetchers.USER_FETCHER
import com.sleepkqq.sololeveling.player.model.entity.user.dto.UserView
import com.sleepkqq.sololeveling.player.service.mapper.ProtoMapper
import com.sleepkqq.sololeveling.player.service.service.user.UserService
import com.sleepkqq.sololeveling.proto.user.AuthUserRequest
import com.sleepkqq.sololeveling.proto.user.GetUserLocaleRequest
import com.sleepkqq.sololeveling.proto.user.GetUserLocaleResponse
import com.sleepkqq.sololeveling.proto.user.GetUserRequest
import com.sleepkqq.sololeveling.proto.user.GetUserResponse
import com.sleepkqq.sololeveling.proto.user.UpdateUserLocaleRequest
import com.sleepkqq.sololeveling.proto.user.UpdateUserLocaleResponse
import com.sleepkqq.sololeveling.proto.user.UserServiceGrpc.UserServiceImplBase
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import org.springframework.grpc.server.service.GrpcService
import java.util.Locale

@Suppress("unused")
@GrpcService
class UserApi(
	private val userService: UserService,
	private val protoMapper: ProtoMapper
) : UserServiceImplBase() {

	private val log = LoggerFactory.getLogger(javaClass)

	override fun getUser(
		request: GetUserRequest,
		responseObserver: StreamObserver<GetUserResponse>
	) {
		log.info(">> getUser called by user={}", request.userId)

		val user = userService.getView(request.userId, UserView::class)
		val response = GetUserResponse.newBuilder()
			.setUser(protoMapper.map(user))
			.build()

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}

	override fun authUser(
		request: AuthUserRequest,
		responseObserver: StreamObserver<Empty>
	) {
		log.info(">> authUser called by user={}", request.user.id)

		val user = protoMapper.map(request.user)
		userService.upsert(user.toEntity())
		val response = Empty.newBuilder().build()

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}

	override fun getUserLocale(
		request: GetUserLocaleRequest,
		responseObserver: StreamObserver<GetUserLocaleResponse>
	) {
		log.info(">> getUserLocale called by user={}", request.userId)

		val user = userService.get(request.userId, USER_FETCHER.locale().manualLocale())
		val locale = Locale.forLanguageTag(user.manualLocale() ?: user.locale())
		val response = GetUserLocaleResponse.newBuilder()
			.setLocale(locale.language)
			.setIsManual(true)
			.build()

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}

	override fun updateUserLocale(
		request: UpdateUserLocaleRequest,
		responseObserver: StreamObserver<UpdateUserLocaleResponse>
	) {
		log.info(">> updateUserLocale called by user={}", request.userId)

		userService.updateLocale(request.userId, Locale.forLanguageTag(request.locale))
		val response = UpdateUserLocaleResponse.newBuilder()
			.setLocale(request.locale)
			.setIsManual(true)
			.build()

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}
}
