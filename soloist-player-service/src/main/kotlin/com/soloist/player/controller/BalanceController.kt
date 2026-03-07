package com.soloist.player.controller

import com.soloist.config.interceptor.UserContextHolder
import com.soloist.jimmer.enums.EnumLocalizer
import com.soloist.player.mapper.ProtoMapper
import com.soloist.player.model.entity.balance.BalanceTransaction.AMOUNT_FIELD
import com.soloist.player.model.entity.balance.dto.BalanceTransactionView
import com.soloist.player.model.entity.balance.dto.BalanceView
import com.soloist.player.model.repository.balance.BalanceTransactionRepository
import com.soloist.player.service.i18n.LocalizationCode
import com.soloist.player.service.balance.BalanceService
import com.soloist.player.service.balance.BalanceTransactionService
import com.soloist.proto.balance.*
import io.grpc.stub.StreamObserver
import org.springframework.grpc.server.service.GrpcService

@GrpcService
class BalanceController(
	private val protoMapper: ProtoMapper,
	private val balanceTransactionService: BalanceTransactionService,
	private val balanceService: BalanceService,
	private val enumLocalizer: EnumLocalizer
) : BalanceServiceGrpc.BalanceServiceImplBase() {

	override fun getBalance(
		request: GetBalanceRequest,
		responseObserver: StreamObserver<GetBalanceResponse>
	) {
		val playerBalance = balanceService.getView(
			request.playerId,
			BalanceView::class
		)
		val response = GetBalanceResponse.newBuilder()
			.setBalance(protoMapper.map(playerBalance))
			.build()

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}

	override fun searchBalanceTransactions(
		request: SearchBalanceTransactionsRequest,
		responseObserver: StreamObserver<SearchBalanceTransactionsResponse>
	) {
		val transactionsPage = balanceTransactionService.searchView(
			UserContextHolder.getUserId()!!,
			request.options,
			request.paging,
			BalanceTransactionView::class
		)
		val response = protoMapper.mapTransactions(
			transactionsPage,
			request.paging.page,
			request.paging.pageSize,
			enumLocalizer.localize(
				LocalizationCode.TABLES_BALANCE_TRANSACTIONS,
				BalanceTransactionRepository.FIELD_ENUM_TYPES
			),
			setOf(AMOUNT_FIELD)
		)

		responseObserver.onNext(response)
		responseObserver.onCompleted()
	}
}
