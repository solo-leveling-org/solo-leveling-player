import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("buildsrc.convention.kotlin-jvm")
	alias(libs.plugins.springDependencyManagement)
	alias(libs.plugins.ksp)
	alias(libs.plugins.kotlinPluginSpring)
	`java-library`
	kotlin("jvm")
	kotlin("kapt")
}

dependencies {
	implementation(libs.bundles.kotlinxEcosystem)
	
	// Spring
	api(libs.springContext)

	// Database
	api(libs.postgresql)
	api(libs.liquibaseCore)
	api(libs.jimmerSpringBootStarter)
	ksp(libs.jimmerKsp)
	kapt(libs.jimmerMapstructApt)
}

kotlin {
	sourceSets.main {
		kotlin.srcDir("build/generated/ksp/main/kotlin")
	}
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.compilerOptions {
	freeCompilerArgs.set(listOf("-Xannotation-default-target=param-property"))
}

tasks.withType<com.google.devtools.ksp.gradle.KspTaskJvm> {
	outputs.cacheIf { false }
}

tasks.withType<Test>().configureEach {
	useJUnitPlatform()
	testLogging {
		events("passed", "skipped", "failed")
	}
}