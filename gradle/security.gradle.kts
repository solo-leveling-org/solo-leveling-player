// Security configuration

// Spring Security configuration
fun configureSpringSecurity(dependencies: DependencyHandlerScope) {
    dependencies.apply {
        implementation("org.springframework.boot:spring-boot-starter-security")
        implementation("org.springframework.security:spring-security-oauth2-resource-server")
        implementation("org.springframework.security:spring-security-oauth2-jose")
    }
}

// JWT configuration
fun configureJWT(dependencies: DependencyHandlerScope) {
    dependencies.apply {
        implementation("io.jsonwebtoken:jjwt-api:0.12.3")
        runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.3")
        runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.3")
    }
}

// CORS configuration
fun configureCORS(dependencies: DependencyHandlerScope) {
    dependencies.apply {
        implementation("org.springframework.boot:spring-boot-starter-web")
    }
}

// Rate limiting configuration
fun configureRateLimiting(dependencies: DependencyHandlerScope) {
    dependencies.apply {
        implementation("com.github.vladimir-bukhtoyarov:bucket4j-core:7.6.0")
    }
}
