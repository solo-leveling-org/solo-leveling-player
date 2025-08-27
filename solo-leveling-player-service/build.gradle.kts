plugins {
	id("buildsrc.convention.kotlin-jvm")
	alias(libs.plugins.springBoot)
	alias(libs.plugins.springDependencyManagement)
	alias(libs.plugins.kotlinPluginSpring)
	kotlin("jvm")
	alias(libs.plugins.kotlinPluginSerialization)
	kotlin("kapt")
	alias(libs.plugins.graalvmNative)
}

dependencyManagement {
	imports {
		mavenBom("org.testcontainers:testcontainers-bom:1.21.3")
		mavenBom("org.springframework.grpc:spring-grpc-dependencies:0.10.0")
	}
}

// Конфигурация для нативной сборки
graalvmNative {
	toolchainDetection.set(true)
	binaries {
		named("main") {
			imageName.set("solo-leveling-player-service")
			mainClass.set("com.sleepkqq.sololeveling.player.service.ApplicationKt")
			fallback.set(false)

			buildArgs.add("--enable-url-protocols=http,https")
			buildArgs.add("-march=compatibility")
		}
	}
}

dependencies {
	implementation(project(":solo-leveling-player-model"))

	// Kotlin ecosystem
	implementation(libs.bundles.kotlinxEcosystem)

	// Spring Boot starters
	implementation(libs.bundles.springBootStarters)
	implementation(libs.springRetry)
	implementation(libs.springAspects)
	implementation(libs.springKafka)
	// Redis
	implementation(libs.springBootStarterDataRedis)
	implementation(libs.lettuce)

	// GRPC
	implementation(platform(libs.springGrpcDependencies))
	implementation(libs.springGrpcSpringBootStarter)

	// Mapstruct
	implementation(libs.mapstruct)
	annotationProcessor(libs.mapstructProcessor)
	kapt(libs.mapstructProcessor)

	// Project dependencies
	implementation(libs.soloLevelingProto)
	implementation(libs.soloLevelingAvro)

	// Test dependencies
	testImplementation(libs.springBootStarterTest)
	testImplementation(libs.bundles.testcontainers)
}