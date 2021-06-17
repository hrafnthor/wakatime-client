import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

buildscript {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.0-beta04")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.0")
        classpath("de.mannodermaus.gradle.plugins:android-junit5:1.7.1.1")
    }
}

plugins {
    // Used for dependency update checking
    id("com.github.ben-manes.versions") version "0.38.0"
    // Native Kotlin Serialization
    kotlin("plugin.serialization") version "1.5.0"
}

allprojects {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
    }
}

tasks {
    val clean by registering(Delete::class) {
        delete(buildDir)
    }
    withType<Test> {
        // For kotest Junit5 compatibility
        useJUnitPlatform()
    }

    // Set dependency update task configuration to filter unstable updates.
    // see https://github.com/ben-manes/gradle-versions-plugin for details
    named<DependencyUpdatesTask>("dependencyUpdates") {
        checkConstraints = true
        checkForGradleUpdate = true
        rejectVersionIf {
            // reject all unstable dependency versions
            isUnstable(candidate.version)
        }
    }
}

fun isUnstable(version: String): Boolean {
    val hasUnstableKeywords = listOf("beta", "alpha", "rc").any {
        version.toLowerCase().contains(it)
    }
    // Capture formats where {number} is repeated with {dot} in between
    val regex = "^(?:[0-9]+\\.)+[0-9]+$".toRegex()
    return hasUnstableKeywords || regex.matches(version).not()
}
