import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    // plugins for Gradle 8.10.1
    id("org.gradle.kotlin.kotlin-dsl") version "4.5.0"
    id("org.jetbrains.kotlin.jvm") version "1.9.24"
}

group = "my.buildlogic"

// Configure the build-logic plugins to target JDK 17
// This matches the JDK used to build the project, and is not related to what is running on device.
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly("com.android.tools.build:gradle:8.6.0")
    compileOnly("com.github.ben-manes:gradle-versions-plugin:0.51.0")
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.24") // version for Gradle 8.10.1
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

@Suppress(
    "ktlint:standard:argument-list-wrapping",
    "ktlint:standard:binary-expression-wrapping",
    "ktlint:standard:function-literal",
    "ktlint:standard:max-line-length",
    "ktlint:standard:wrapping",
)

gradlePlugin {
    plugins {
        register("android-library") { id = "demo.android-library"; implementationClass = "AndroidLibraryConventionPlugin" }
        register("jvm-library") { id = "demo.jvm-library"; implementationClass = "JvmLibraryConventionPlugin" }
        register("versions") { id = "demo.versions"; implementationClass = "VersionsConventionPlugin" }
    }
}
