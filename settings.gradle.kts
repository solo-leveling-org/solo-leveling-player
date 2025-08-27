rootProject.name = "solo-leveling-player"

include(":solo-leveling-player-service")
include(":solo-leveling-player-model")

val githubUser = extra["github.user"].toString()
val githubToken = extra["github.token"].toString()

dependencyResolutionManagement {
	@Suppress("UnstableApiUsage")
	repositories {
		mavenCentral()
		gradlePluginPortal()
		maven { url = uri("https://packages.confluent.io/maven/") }
		maven { url = uri("https://repo.spring.io/milestone") }
		maven { url = uri("https://repo.spring.io/snapshot") }
		maven {
			url = uri("https://maven.pkg.github.com/solo-leveling-org/solo-leveling-proto")
			credentials {
				username = githubUser
				password = githubToken
			}
		}
		maven {
			url = uri("https://maven.pkg.github.com/solo-leveling-org/solo-leveling-avro")
			credentials {
				username = githubUser
				password = githubToken
			}
		}
		maven {
			url = uri("https://raw.githubusercontent.com/graalvm/native-build-tools/snapshots")
		}
	}
}