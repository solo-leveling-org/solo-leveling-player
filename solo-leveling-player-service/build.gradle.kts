plugins {
	id("buildsrc.convention.kotlin-jvm")
	id("org.springframework.boot") version "3.5.0"
	id("io.spring.dependency-management") version "1.1.4"
	kotlin("plugin.spring") version "2.2.0"
	kotlin("jvm")
	alias(libs.plugins.kotlinPluginSerialization)
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
	implementation("org.springframework.kafka:spring-kafka")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-actuator")

	// GRPC
	implementation(platform("org.springframework.grpc:spring-grpc-dependencies:0.10.0-SNAPSHOT"))
	implementation("org.springframework.grpc:spring-grpc-spring-boot-starter")
	implementation("io.grpc:grpc-netty-shaded")
	modules {
		module("io.grpc:grpc-netty") {
			replacedBy("io.grpc:grpc-netty-shaded", "Use Netty shaded instead of regular Netty")
		}
	}

	// Project modules
	implementation(project(":solo-leveling-player-model"))
	implementation("com.sleepkqq:solo-leveling-proto:2.1.0")
	implementation("com.sleepkqq:solo-leveling-avro:2.1.0")

	// Tests
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:kafka")
	testImplementation("org.testcontainers:junit-jupiter")
}