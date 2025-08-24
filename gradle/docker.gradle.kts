// Docker configuration
import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import com.bmuschko.gradle.docker.tasks.image.DockerPushImage

// Docker build configuration
fun configureDockerBuild(task: DockerBuildImage, imageName: String, tag: String = "latest") {
    task.apply {
        images.add("$imageName:$tag")
        images.add("$imageName:${project.version}")
        images.add("$imageName:${project.findProperty("git.commit.id.abbrev") ?: "latest"}")
    }
}

// Docker push configuration
fun configureDockerPush(task: DockerPushImage, imageName: String, tag: String = "latest") {
    task.apply {
        images.add("$imageName:$tag")
        images.add("$imageName:${project.version}")
        images.add("$imageName:${project.findProperty("git.commit.id.abbrev") ?: "latest"}")
    }
}

// Docker registry configuration
fun configureDockerRegistry(extension: com.bmuschko.gradle.docker.DockerExtension) {
    extension.registryCredentials {
        url.set(project.findProperty("docker.registry.url") as String?)
        username.set(project.findProperty("docker.registry.username") as String?)
        password.set(project.findProperty("docker.registry.password") as String?)
        email.set(project.findProperty("docker.registry.email") as String?)
    }
}
