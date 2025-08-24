// Profiles configuration

// Development profile configuration
fun configureDevelopmentProfile(dependencies: DependencyHandlerScope) {
    dependencies.apply {
        runtimeOnly("com.h2database:h2")
        runtimeOnly("org.springframework.boot:spring-boot-devtools")
    }
}

// Production profile configuration
fun configureProductionProfile(dependencies: DependencyHandlerScope) {
    dependencies.apply {
        implementation("org.springframework.boot:spring-boot-starter-actuator")
        implementation("io.micrometer:micrometer-registry-prometheus")
    }
}

// Test profile configuration
fun configureTestProfile(dependencies: DependencyHandlerScope) {
    dependencies.apply {
        testImplementation("com.h2database:h2")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("org.testcontainers:junit-jupiter")
    }
}

// Profile-specific task configuration
fun configureProfileTasks(tasks: TaskContainer, profile: String) {
    tasks.withType<org.springframework.boot.gradle.tasks.run.BootRun> {
        systemProperty("spring.profiles.active", profile)
    }
    
    tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
        systemProperty("spring.profiles.active", profile)
    }
}
