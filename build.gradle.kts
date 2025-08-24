// Корневой build.gradle.kts для управления профилями сборки
plugins {
	id("base")
}

// Профили сборки
val buildProfile = providers.gradleProperty("buildProfile").orElse("jvm")

// Задача для проверки профиля
tasks.register("showBuildProfile") {
	group = "build"
	description = "Показывает текущий профиль сборки"
	// Отключаем configuration cache для этой задачи
	notCompatibleWithConfigurationCache("Task uses Gradle script object references")
	doLast {
		println("Текущий профиль сборки: $buildProfile")
	}
}

// Задача для сборки JVM версии
tasks.register("buildJvm") {
	group = "build"
	description = "Собирает JVM версию проекта"
	dependsOn(":solo-leveling-player-model:build")
	dependsOn(":solo-leveling-player-service:bootJar")
}

// Задача для сборки нативной версии
tasks.register("buildNative") {
	group = "build"
	description = "Собирает нативную версию проекта"
	dependsOn(":solo-leveling-player-model:build")
	dependsOn(":solo-leveling-player-service:nativeCompile")
}

// Задача для сборки в зависимости от профиля
tasks.register("buildWithProfile") {
	group = "build"
	description = "Собирает проект в соответствии с профилем"
	// Отключаем configuration cache для этой задачи
	notCompatibleWithConfigurationCache("Task uses Gradle script object references")
	dependsOn(if (buildProfile.get() == "native") "buildNative" else "buildJvm")
}

// Задача для очистки
tasks.register("cleanAll") {
	group = "build"
	description = "Очищает все модули проекта"
	dependsOn(":solo-leveling-player-model:clean")
	dependsOn(":solo-leveling-player-service:clean")
}
