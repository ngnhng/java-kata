// Defines "What" to build.
// https://docs.gradle.org/9.3.1/kotlin-dsl/gradle/org.gradle.api.initialization/-settings/index.html

// The object returned by rootProject is mutable (it has a setName method).
// The Kotlin compiler actually translates it to this Java bytecode:
//   getRootProject().setName("...");
rootProject.name = "java-kata"

pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }

  // put internal Gradle plugin projects here
  // this makes local convention plugins resolvable during the plugins {} phase
  // and avoids publishing plugins to an external repository just to use them internally
  includeBuild("build-logic")
}

// https://docs.gradle.org/9.3.1/userguide/best_practices_dependencies.html#do_this_instead_2
dependencyResolutionManagement {
  // If this mode is set, any repository declared directly in a project, either directly or via a
  // plugin, will trigger a build error.
  // See:
  // https://docs.gradle.org/9.3.1/kotlin-dsl/gradle/org.gradle.api.initialization.resolve/-repositories-mode/index.html
  repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
  repositories {
    mavenLocal()
    mavenCentral()
  }
}

// Adds the given projects to the build. Each path in the supplied list is treated as the path of a
// project to add to the build.
// Note that these path are not file paths, but instead specify the location of the new project in
// the project hierarchy. As such, the supplied paths must use the ':' character as separator (and
// NOT '/').
// See:
// https://docs.gradle.org/9.3.1/kotlin-dsl/gradle/org.gradle.api.initialization/-settings/include.html
include(
    ":01-functional-shift:01-declarative-aggregator",
)
