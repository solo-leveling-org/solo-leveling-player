package com.sleepkqq.sololeveling.player.service.config

import com.sleepkqq.sololeveling.proto.config.LocaleServerInterceptor
import io.grpc.ServerInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.grpc.server.GlobalServerInterceptor

@Suppress("unused")
@Configuration
class GrpcConfig {

	@Bean
	@Order(100)
	@GlobalServerInterceptor
	fun localeServerInterceptor(): ServerInterceptor = LocaleServerInterceptor()
}
