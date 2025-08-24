// Common configuration for all modules
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.tasks.testing.logging.TestLogEvent

// Common plugins configuration
fun configureCommonPlugins(plugins: PluginDependenciesSpec) {
    plugins.apply {
        id("buildsrc.convention.kotlin-jvm")
        kotlin("jvm")
        alias(libs.plugins.kotlinPluginSerialization)
    }
}

// Common dependencies configuration
fun configureCommonDependencies(dependencies: DependencyHandlerScope) {
    dependencies.apply {
        implementation(libs.bundles.kotlinxEcosystem)
    }
}

// Common Kotlin configuration
fun configureKotlin(compileKotlin: KotlinCompile) {
    compileKotlin.compilerOptions {
        freeCompilerArgs.set(listOf("-Xannotation-default-target=param-property"))
        jvmTarget.set("24")
    }
}

// Common test configuration
fun configureTests(tasks: TaskContainer) {
    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
        testLogging {
            events(
                TestLogEvent.FAILED,
                TestLogEvent.PASSED,
                TestLogEvent.SKIPPED
            )
        }
    }
}

// Common KSP configuration
fun configureKsp(tasks: TaskContainer) {
    tasks.withType<com.google.devtools.ksp.gradle.KspTaskJvm> {
        outputs.cacheIf { false }
    }
}

// Common Kotlin source sets configuration
fun configureKotlinSourceSets(kotlin: org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension) {
    kotlin.sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
}
