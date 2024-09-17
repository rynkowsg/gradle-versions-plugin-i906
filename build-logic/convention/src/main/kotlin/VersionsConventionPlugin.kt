import GuavaCheckResult.GUAVA_CANDIDATE_NEWER
import GuavaCheckResult.GUAVA_CANDIDATE_OLDER
import GuavaCheckResult.GUAVA_DIFFERENT_FLAVORS
import GuavaCheckResult.GUAVA_SAME_VERSIONS
import GuavaCheckResult.NOT_GUAVA
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.benmanes.gradle.versions.updates.resolutionstrategy.ComponentSelectionWithCurrent
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType

val isDEBUG: Boolean = System.getenv("DEBUG").let { it in listOf("true", "1") }.also { println("DEBUG=$it") }

fun log(message: String) {
    if (isDEBUG) {
        println(message)
    }
}

private data class VersionComparisonData(
    val group: String?,
    val module: String?,
    val oldVersion: String,
    val newVersion: String,
)

class VersionsConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        println("Applying VersionsConventionPlugin to $name")
        pluginManager.apply("com.github.ben-manes.versions")

        tasks.withType<DependencyUpdatesTask>().configureEach {
            checkForGradleUpdate = true
            outputFormatter = "plain,html"
            outputDir = "build/dependencyUpdates"
            reportfileName = "report"
            val filter: ComponentSelectionWithCurrent.() -> Boolean = {
                val candidate = this
//                log("this: $this")
//                log("candidate?.module: ${candidate.candidate?.module}")
//                log("candidate?.group: ${candidate.candidate?.group}")
//                log("candidate?.version: ${candidate.candidate?.version}")
//                log("candidate?.displayName: ${candidate.candidate?.displayName}")
//                log("candidate?.moduleIdentifier: ${candidate.candidate?.moduleIdentifier}")
                val comparisonData = VersionComparisonData(
                    group = candidate.candidate.group,
                    module = candidate.candidate.module,
                    oldVersion = candidate.currentVersion,
                    newVersion = candidate.candidate.version,
                )
                log("comparisonData: $comparisonData")
                val shouldReject = !shouldApprove(comparisonData)
                log("shouldReject: $shouldReject")
                log("------------------------------------------------------------")
                shouldReject
            }
            rejectVersionIf(filter)
        }
    }
}

private fun shouldApprove(data: VersionComparisonData): Boolean {
    val foundResult = checkIfGuavaLibrary(data)
    log("foundResult: $foundResult")
    when (foundResult) {
        GUAVA_SAME_VERSIONS -> return true // we don't want to update to the same version
        // different flavours should not be compared
        // e.g. 32.1.3-android, 32.1.3-jre - there is no new version in such case
        GUAVA_DIFFERENT_FLAVORS -> return false
        GUAVA_CANDIDATE_NEWER -> return true // approve newer version
        GUAVA_CANDIDATE_OLDER -> return false // reject older version
        NOT_GUAVA -> {} // do nothing
    }
    // approve new version if:
    // - the new is stable, or
    // - the current is unstable (if current is unstable, new can be either stable or unstable)
    val shouldApprove = isStable(data.newVersion) || !isStable(data.oldVersion)
    return shouldApprove
}

// Compares whether two versions are same just different by flavour.
// It is to deal with situation when plugin suggest to upgrade -android
// Guava version to -jre version, e.g. from `guava-25.1-android` to `guava-25.1-jre`.
private fun checkIfGuavaLibrary(data: VersionComparisonData): GuavaCheckResult {
    if (data.group != "com.google.guava") {
        return NOT_GUAVA
    }
    val (oldVer, oldFlavor) = parseGuavaVersion(data.oldVersion) ?: Pair(null, null)
    val (newVer, newFlavor) = parseGuavaVersion(data.newVersion) ?: Pair(null, null)
    if (oldVer == null || newVer == null || oldFlavor == null || newFlavor == null) {
        return NOT_GUAVA // problems when parsing - treat as not Guava
    }
    if (oldFlavor != newFlavor) {
        return GUAVA_DIFFERENT_FLAVORS // if not the same flavors, they should not be compared
    }
    val same = oldVer == newVer
    if (same) return GUAVA_SAME_VERSIONS
    // TODO: improve the comparison
    return if (newVer > oldVer) GUAVA_CANDIDATE_NEWER else GUAVA_CANDIDATE_OLDER
}

private enum class GuavaCheckResult {
    NOT_GUAVA,
    GUAVA_DIFFERENT_FLAVORS,
    GUAVA_SAME_VERSIONS,
    GUAVA_CANDIDATE_NEWER,
    GUAVA_CANDIDATE_OLDER,
}

private fun parseGuavaVersion(version: String): Pair<String?, String?>? {
    val pattern = Regex("^(.*?)(-android|-jre)$").find(version)
    return pattern?.let { Pair(it.groupValues[1], it.groupValues[2]) }
}

private fun isStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { it in version.uppercase() }
    val regex = Regex("^[0-9,.v-]+(-r)?$")
    return stableKeyword || regex.matches(version)
}
