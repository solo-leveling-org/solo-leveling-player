package com.soloist.player.grpc.server

import com.google.protobuf.Empty
import com.soloist.config.interceptor.UserContextHolder
import com.soloist.player.mapper.ProtoMapper
import com.soloist.player.model.entity.player.dto.PlayerDayStreakView
import com.soloist.player.model.entity.user.dto.LocaleUserView
import com.soloist.player.model.entity.user.dto.UserAdditionalInfoView
import com.soloist.player.model.entity.user.dto.UserView
import com.soloist.player.service.user.UserService
import com.soloist.proto.user.*
import io.grpc.stub.StreamObserver
import org.springframework.grpc.server.service.GrpcService
import java.util.*

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

	override fun getUserAdditionalInfo(
		request: Empty,
		responseObserver: StreamObserver<GetUserAdditionalInfoResponse>
	) {
		val user = userService.getView(
			UserContextHolder.getUserId()!!,
			UserAdditionalInfoView::class
		)

		val response = GetUserAdditionalInfoResponse.newBuilder()
			.setPhotoUrl(user.photoUrl)
			.setDayStreak(protoMapper.map(PlayerDayStreakView(user.player.dayStreak.toEntity())))
			.setLocale(
				UserLocale.newBuilder()
					.setTag(user.manualLocale ?: user.locale)
					.setIsManual(user.manualLocale != null)
			)
			.addAllRoles(protoMapper.map(user.roles.map { it.toEntity() }))
			.build()

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}

	override fun updateUserLocale(
		request: UpdateUserLocaleRequest,
		responseObserver: StreamObserver<Empty>
	) {
		userService.updateLocale(
			UserContextHolder.getUserId()!!,
			Locale.forLanguageTag(request.tag)
		)

		responseObserver.onNext(Empty.newBuilder().build())
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
		val response = protoMapper.mapLeaderboardUsers(
			leaderboardPage,
			request.paging.page
		)

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}

	override fun getUserLeaderboard(
		request: GetUserLeaderboardRequest,
		responseObserver: StreamObserver<GetUserLeaderboardResponse>
	) {
		val leaderboardUser = userService.getLeaderboardUser(
			UserContextHolder.getUserId()!!,
			request.type,
			protoMapper.map(request.range)
		)
		val response = GetUserLeaderboardResponse.newBuilder()
			.setUser(protoMapper.map(leaderboardUser))
			.build()

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}

	override fun getUsersStats(
		request: Empty,
		responseObserver: StreamObserver<GetUsersStatsResponse>
	) {
		val usersStats = userService.getUsersStats()
		val response = protoMapper.map(usersStats)

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}

	override fun getUsers(
		request: GetUsersRequest,
		responseObserver: StreamObserver<GetUsersResponse>
	) {
		val usersPage = userService.getUsers(request.paging, LocaleUserView::class)
		val response = protoMapper.mapLocaleUsers(usersPage, request.paging.page)

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}
}