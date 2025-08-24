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
		mavenBom("org.springframework.grpc:spring-grpc-dependencies:0.10.0-SNAPSHOT")
	}
}

// Конфигурация для нативной сборки
graalvmNative {
	binaries {
		named("main") {
			imageName.set("solo-leveling-player-service")
			buildArgs.add("--no-fallback")
			buildArgs.add("-H:Class=com.sleepkqq.sololeveling.player.service.ApplicationKt")
			buildArgs.add("-H:+ReportExceptionStackTraces")
			buildArgs.add("-H:EnableURLProtocols=http,https")
			buildArgs.add("--initialize-at-run-time=kotlin.reflect.jvm.ReflectJvmMapping,kotlin.reflect.jvm.internal.ReflectionFactoryImpl,kotlin.reflect.jvm.internal.KotlinReflectionInternalError")
			buildArgs.add("--initialize-at-run-time=org.springframework.grpc.server.service.GrpcService")
			buildArgs.add("--initialize-at-run-time=io.grpc.stub.StreamObserver")
			buildArgs.add("--initialize-at-run-time=com.google.protobuf.Empty")
			// Добавляем флаг для экспериментальных опций
			buildArgs.add("-H:+UnlockExperimentalVMOptions")
			// Убираем неподдерживаемые опции
			// buildArgs.add("-H:+OptimizeStringConcat")
			// buildArgs.add("-H:+AllowIncompleteClasspath")
			
			// Дополнительные настройки для улучшения совместимости
			buildArgs.add("--enable-http")
			buildArgs.add("--enable-https")
			buildArgs.add("--enable-all-security-services")
			buildArgs.add("--report-unsupported-elements-at-runtime")
			buildArgs.add("--allow-incomplete-classpath")
		}
	}
}

// Отключаем AOT для JVM сборки
springBoot {
	buildInfo()
}

dependencies {
	implementation(project(":solo-leveling-player-model"))
	
	// Kotlin ecosystem
	implementation(libs.bundles.kotlinxEcosystem)
	implementation(libs.kotlinReflect)
	
	// Spring Boot starters
	implementation(libs.bundles.springBootStarters)
	implementation(libs.springRetry)
	implementation(libs.springAspects)
	implementation(libs.springKafka)
	implementation(libs.springBootStarterDataRedis)
	
	// GRPC
	implementation(libs.springGrpcSpringBootStarter)
	
	// Serialization & Mapping
	implementation(libs.bundles.kotlinxCoroutines)
	implementation(libs.mapstruct)
	kapt(libs.mapstructProcessor)
	
	// Project dependencies
	implementation(libs.soloLevelingProto)
	implementation(libs.soloLevelingAvro)
	
	// Test dependencies
	testImplementation(libs.bundles.springBootTest)
	testImplementation(libs.bundles.testcontainers)
}

// Отключаем AOT для JVM сборки
tasks.matching { it.name.contains("processAot") }.configureEach {
	enabled = false
}

// Отключаем configuration cache для нативных задач
tasks.matching { it.name.contains("native") || it.name.contains("generateResourcesConfigFile") }.configureEach {
	notCompatibleWithConfigurationCache("Native build tasks are not compatible with configuration cache")
}