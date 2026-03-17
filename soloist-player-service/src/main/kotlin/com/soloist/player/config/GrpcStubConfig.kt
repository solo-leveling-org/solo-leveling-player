package com.soloist.player.config

import com.soloist.proto.agent.AgentServiceGrpc
import com.soloist.proto.agent.AgentServiceGrpc.AgentServiceBlockingStub
import com.soloist.proto.config.GrpcChannelFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GrpcStubConfig(private val grpc: GrpcChannelFactory) {

	@Bean
	fun agentStub(): AgentServiceBlockingStub =
		grpc.stub("agent") { AgentServiceGrpc.newBlockingStub(it) }
}
