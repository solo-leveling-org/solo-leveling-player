package com.sleepkqq.sololeveling.player.grpc.server

import com.google.protobuf.Empty
import com.sleepkqq.sololeveling.config.interceptor.UserContextHolder
import com.sleepkqq.sololeveling.player.model.entity.Fetchers
import com.sleepkqq.sololeveling.player.model.entity.user.dto.UserView
import com.sleepkqq.sololeveling.player.mapper.ProtoMapper
import com.sleepkqq.sololeveling.player.service.user.UserService
import com.sleepkqq.sololeveling.proto.user.AuthUserRequest
import com.sleepkqq.sololeveling.proto.user.GetUserRequest
import com.sleepkqq.sololeveling.proto.user.GetUserResponse
import com.sleepkqq.sololeveling.proto.user.GetUsersLeaderboardRequest
import com.sleepkqq.sololeveling.proto.user.GetUsersLeaderboardResponse
import com.sleepkqq.sololeveling.proto.user.UpdateUserLocaleRequest
import com.sleepkqq.sololeveling.proto.user.UserLocaleResponse
import com.sleepkqq.sololeveling.proto.user.UserServiceGrpc
import io.grpc.stub.StreamObserver
import org.springframework.grpc.server.service.GrpcService
import java.util.Locale

@GrpcService
class UserApi(
	private val userService: UserService,
	private val protoMapper: ProtoMapper
) : UserServiceGrpc.UserServiceImplBase() {

	override fun getUser(
		request: GetUserRequest,
		responseObserver: StreamObserver<GetUserResponse>
	) {
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
		val user = protoMapper.map(request.user)
		userService.upsert(user.toEntity())
		val response = Empty.newBuilder().build()

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}

	override fun getUserLocale(
		request: Empty,
		responseObserver: StreamObserver<UserLocaleResponse>
	) {
		val user = userService.get(
			UserContextHolder.getUserId()!!,
			Fetchers.USER_FETCHER.locale().manualLocale()
		)
		val locale = user.manualLocale() ?: user.locale()
		val isManual = user.manualLocale() != null

		val response = UserLocaleResponse.newBuilder()
			.setLocale(locale)
			.setIsManual(isManual)
			.build()

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}

	override fun updateUserLocale(
		request: UpdateUserLocaleRequest,
		responseObserver: StreamObserver<UserLocaleResponse>
	) {
		userService.updateLocale(
			UserContextHolder.getUserId()!!,
			Locale.forLanguageTag(request.locale)
		)
		val response = UserLocaleResponse.newBuilder()
			.setLocale(request.locale)
			.setIsManual(true)
			.build()

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}

	override fun getUsersLeaderboard(
		request: GetUsersLeaderboardRequest,
		responseObserver: StreamObserver<GetUsersLeaderboardResponse>
	) {
		val leaderboardPage = userService.getLeaderboardPage(
			request.type,
			protoMapper.map(request.range),
			request.paging
		)
		val response = protoMapper.map(leaderboardPage, request.paging.page)

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}
}