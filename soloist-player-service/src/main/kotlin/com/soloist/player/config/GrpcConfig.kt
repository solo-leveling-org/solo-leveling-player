package com.soloist.player.config

import com.soloist.jimmer.enums.EnumLocalizer
import com.soloist.proto.config.interceptor.UserServerInterceptor
import io.grpc.ServerInterceptor
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.grpc.server.GlobalServerInterceptor

@Configuration
class GrpcConfig {

	@Bean
	@Order(100)
	@GlobalServerInterceptor
	fun localeServerInterceptor(): ServerInterceptor = UserServerInterceptor()

	@Bean
	fun enumLocalizer(messageSource: MessageSource): EnumLocalizer = EnumLocalizer(messageSource)
}
