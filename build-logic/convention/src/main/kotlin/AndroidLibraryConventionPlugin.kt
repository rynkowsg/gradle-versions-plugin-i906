import com.android.build.gradle.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            println("Applying AndroidLibraryConventionPlugin to $name")
            pluginManager.apply("com.android.library")

            extensions.configure<LibraryExtension> {
                buildToolsVersion = "34.0.0"
                compileSdk = 34
                defaultConfig {
                    minSdk = 21
                    vectorDrawables.useSupportLibrary = true
                }
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_1_8
                    targetCompatibility = JavaVersion.VERSION_1_8
                }
                testOptions.animationsDisabled = true
                testOptions.targetSdk = 34
            }
        }
    }
}
