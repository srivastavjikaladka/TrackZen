pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("com.android.application") version "8.6.1" apply false
        id("org.jetbrains.kotlin.android") version "1.9.0" apply false
        id("org.jetbrains.kotlin.kapt") version "1.9.0" apply false
        id("androidx.navigation.safeargs.kotlin") version "2.7.7" apply false
        id("com.google.dagger.hilt.android") version "2.48" // ✅ ADD THIS LINE
    }

}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
        }
    }

    rootProject.name = "TrackZen"
    include(":app")
}
