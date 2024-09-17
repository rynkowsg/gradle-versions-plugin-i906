dependencyResolutionManagement {
    repositories {
        google()
        gradlePluginPortal() // for gradle-versions-plugin
        mavenCentral()
    }
}

rootProject.name = "build-logic"
include(":convention")
