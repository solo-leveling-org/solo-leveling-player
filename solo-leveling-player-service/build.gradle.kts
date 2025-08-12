plugins {
	id("buildsrc.convention.kotlin-jvm")
	id("org.springframework.boot") version "3.5.0"
	id("io.spring.dependency-management") version "1.1.4"
	kotlin("plugin.spring") version "2.2.0"
	kotlin("jvm")
	alias(libs.plugins.kotlinPluginSerialization)
	kotlin("kapt")
}

dependencyManagement {
	imports {
		mavenBom("org.testcontainers:testcontainers-bom:1.21.3")
	}
}

dependencies {
	implementation(libs.bundles.kotlinxEcosystem)
	// Spring Boot
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.retry:spring-retry")
	implementation("org.springframework:spring-aspects")

	// Kafka
	implementation("org.springframework.kafka:spring-kafka")

	// Redis
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("io.lettuce:lettuce-core")

	// GRPC
	implementation(platform("org.springframework.grpc:spring-grpc-dependencies:0.10.0-SNAPSHOT"))
	implementation("org.springframework.grpc:spring-grpc-spring-boot-starter")
	implementation("io.grpc:grpc-netty-shaded")
	modules {
		module("io.grpc:grpc-netty") {
			replacedBy("io.grpc:grpc-netty-shaded", "Use Netty shaded instead of regular Netty")
		}
	}

	// Mapstruct
	implementation("org.mapstruct:mapstruct:1.6.3")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")
	kapt("org.mapstruct:mapstruct-processor:1.6.3")

	// Project modules
	implementation(project(":solo-leveling-player-model"))
	implementation("com.sleepkqq:solo-leveling-proto:4.2.1")
	implementation("com.sleepkqq:solo-leveling-avro:2.1.4")

	// Tests
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:kafka")
	testImplementation("org.testcontainers:postgresql")
	testImplementation("org.testcontainers:junit-jupiter")
}