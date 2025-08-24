// Spring Boot specific configuration
import org.springframework.boot.gradle.tasks.bundling.BootJar

// Spring Boot plugins configuration
fun configureSpringBootPlugins(plugins: PluginDependenciesSpec) {
    plugins.apply {
        alias(libs.plugins.springBoot)
        alias(libs.plugins.springDependencyManagement)
        alias(libs.plugins.kotlinPluginSpring)
        alias(libs.plugins.graalvmNative)
    }
}

// Spring Boot dependencies configuration
fun configureSpringBootDependencies(dependencies: DependencyHandlerScope) {
    dependencies.apply {
        // Spring Boot starters
        implementation(libs.bundles.springBootStarters)
        implementation(libs.springRetry)
        implementation(libs.springAspects)
        
        // Spring Boot test
        testImplementation(libs.bundles.springBootTest)
    }
}

// Spring Boot tasks configuration
fun configureSpringBootTasks(tasks: TaskContainer) {
    tasks.withType<BootJar> {
        archiveFileName.set("${project.name}.jar")
    }
}

// GraalVM Native configuration
fun configureGraalvmNative(extension: org.graalvm.buildtools.gradle.dsl.GraalVMExtension) {
    extension.binaries {
        named("main") {
            imageName.set("${project.name}")
            buildArgs.add("--no-fallback")
            buildArgs.add("-H:+ReportExceptionStackTraces")
            buildArgs.add("-H:EnableURLProtocols=http,https")
            buildArgs.add("--initialize-at-run-time=kotlin.reflect.jvm.ReflectJvmMapping,kotlin.reflect.jvm.internal.ReflectionFactoryImpl,kotlin.reflect.jvm.internal.KotlinReflectionInternalError")
        }
    }
}
