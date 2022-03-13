import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

fun isUnstable(version: String): Boolean {
    val hasUnstableKeywords = listOf("beta", "alpha", "rc").any {
        version.toLowerCase().contains(it)
    }
    // Capture formats where {number} is repeated with {dot} in between
    val regex = "^(?:[0-9]+\\.)+[0-9]+$".toRegex()
    return hasUnstableKeywords || regex.matches(version).not()
}

fun ExtraPropertiesExtension.copy(key: String, map: MutableMap<String, String>){
    set(key, map.getOrDefault(key, ""))
}

val localProps = project.rootProject.file("local.properties")
if (localProps.exists()) {
    val props = java.util.Properties()
    java.io.FileInputStream(localProps).bufferedReader().use {
        props.load(it)
    }
    props.forEach { key, value ->
        extra.set(key as String, value)
    }
} else {
    extra.apply {
        val env = System.getenv()
        copy("sonartypeStagingProfileId", env)
        copy("ossrhUsername", env)
        copy("ossrhPassword", env)
        copy("signing.keyId", env)
        copy("signing.password", env)
        copy("signing.secretKeyRingFile", env)
    }
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.1.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.0")
        classpath("de.mannodermaus.gradle.plugins:android-junit5:1.7.1.1")
    }
}

plugins {
    // Used for dependency update checking
    id("com.github.ben-manes.versions") version "0.38.0"
    // Native Kotlin Serialization
    kotlin("plugin.serialization") version "1.5.0"
    // static code analysis for Kotlin
    id("io.gitlab.arturbosch.detekt").version("1.17.1")
    // used for publishing into nexus repositories
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

detekt {
    input = files(rootDir)
    config = files("detekt.yml")
}


tasks {
    withType<Test> {
        // For kotest Junit5 compatibility
        useJUnitPlatform()
    }

    // Set dependency update task configuration to filter unstable updates.
    // see https://github.com/ben-manes/gradle-versions-plugin for details
    register("dependencyUpdated", DependencyUpdatesTask::class) {
        checkConstraints = true
        checkForGradleUpdate = true
        rejectVersionIf {
            // reject all unstable dependency versions
            isUnstable(candidate.version)
        }
    }
}

nexusPublishing {
    repositories {
        sonatype {
            // Values previously read from local.properties
            stagingProfileId.set(extra["sonartypeStagingProfileId"] as String)
            username.set(extra["ossrhUsername"] as String)
            password.set(extra["ossrhPassword"] as String)

            // Different nexus url requirements for signups made post 24th of february
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}