rootProject.name = "solo-leveling-player"

include(":solo-leveling-player-service")
include(":solo-leveling-player-model")

dependencyResolutionManagement {
	@Suppress("UnstableApiUsage")
	repositories {
		mavenCentral()
		gradlePluginPortal()
		maven { url = uri("https://packages.confluent.io/maven/") }
		maven {
			url = uri("https://maven.pkg.github.com/solo-leveling-org/solo-leveling-proto")
			credentials {
				username = System.getenv("GITHUB_USER")
				password = System.getenv("GITHUB_PASSWORD")
			}
		}
		maven {
			url = uri("https://maven.pkg.github.com/solo-leveling-org/solo-leveling-avro")
			credentials {
				username = System.getenv("GITHUB_USER")
				password = System.getenv("GITHUB_PASSWORD")
			}
		}
		maven { url = uri("https://repo.spring.io/milestone") }
		maven { url = uri("https://repo.spring.io/snapshot") }
	}
}