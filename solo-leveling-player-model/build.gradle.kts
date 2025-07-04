plugins {
	id("buildsrc.convention.kotlin-jvm")
	id("io.spring.dependency-management") version "1.1.4"
	id("com.google.devtools.ksp") version "2.2.0-2.0.2"
	kotlin("jvm")
	kotlin("plugin.spring") version "2.2.0"
	alias(libs.plugins.kotlinPluginSerialization)
	`java-library`
}

dependencies {
	api(libs.bundles.kotlinxEcosystem)
	// Spring
	api("org.springframework:spring-context")

	// Database
	api("org.postgresql:postgresql:42.7.7")
	api("org.liquibase:liquibase-core:4.32.0")
	api("org.babyfish.jimmer:jimmer-spring-boot-starter:0.9.96")
	ksp("org.babyfish.jimmer:jimmer-ksp:0.9.96")
}

kotlin {
	sourceSets.main {
		kotlin.srcDir("build/generated/ksp/main/kotlin")
	}
}