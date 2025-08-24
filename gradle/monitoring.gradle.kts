// Monitoring and metrics configuration

// Micrometer configuration
fun configureMicrometer(dependencies: DependencyHandlerScope) {
    dependencies.apply {
        implementation("io.micrometer:micrometer-registry-prometheus")
        implementation("io.micrometer:micrometer-core")
    }
}

// Actuator configuration
fun configureActuator(dependencies: DependencyHandlerScope) {
    dependencies.apply {
        implementation("org.springframework.boot:spring-boot-starter-actuator")
    }
}

// Health check configuration
fun configureHealthChecks(dependencies: DependencyHandlerScope) {
    dependencies.apply {
        implementation("org.springframework.boot:spring-boot-starter-actuator")
        implementation("org.springframework.boot:spring-boot-starter-validation")
    }
}

// Logging configuration
fun configureLogging(dependencies: DependencyHandlerScope) {
    dependencies.apply {
        implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
        implementation("ch.qos.logback:logback-classic")
    }
}
