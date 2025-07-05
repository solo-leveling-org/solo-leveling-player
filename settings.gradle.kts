rootProject.name = "solo-leveling-player"

include(":solo-leveling-player-service")
include(":solo-leveling-player-model")

val env = System.getenv()

dependencyResolutionManagement {
	@Suppress("UnstableApiUsage")
	repositories {
		mavenCentral()
		gradlePluginPortal()
		maven { url = uri("https://packages.confluent.io/maven/") }
		maven {
			url = uri("https://maven.pkg.github.com/solo-leveling-org/solo-leveling-proto")
			credentials {
				username = env["GITHUB_USER"]
				password = env["GITHUB_TOKEN"]
			}
		}
		maven {
			url = uri("https://maven.pkg.github.com/solo-leveling-org/solo-leveling-avro")
			credentials {
				username = env["GITHUB_USER"]
				password = env["GITHUB_TOKEN"]
			}
		}
		maven { url = uri("https://repo.spring.io/milestone") }
		maven { url = uri("https://repo.spring.io/snapshot") }
	}
}