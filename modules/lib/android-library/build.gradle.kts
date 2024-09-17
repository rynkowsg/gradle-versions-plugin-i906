plugins {
    id("demo.android-library")
}

android {
    namespace = "demo.android_library"
}

dependencies {
    implementation("com.google.guava:guava:33.3.0-android")
    implementation(project(":modules:lib:jvm-library"))
}
