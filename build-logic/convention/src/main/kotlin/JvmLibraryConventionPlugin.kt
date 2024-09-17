import org.gradle.api.Plugin
import org.gradle.api.Project

class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            println("Applying JvmLibraryConventionPlugin to $name")
            pluginManager.apply("org.jetbrains.kotlin.jvm")
        }
    }
}
