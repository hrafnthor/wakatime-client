import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.1.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
        classpath("de.mannodermaus.gradle.plugins:android-junit5:1.8.2.0")
    }
}

plugins {
    // Used for dependency update checking
    id("com.github.ben-manes.versions") version "0.42.0"
    // Native Kotlin Serialization
    kotlin("plugin.serialization") version "1.6.10"
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

fun isUnstable(version: String): Boolean {
    val hasUnstableKeywords = listOf("beta", "alpha", "rc").any {
        version.toLowerCase().contains(it)
    }
    // Capture formats where {number} is repeated with {dot} in between
    val regex = "^(?:[0-9]+\\.)+[0-9]+$".toRegex()
    return hasUnstableKeywords || regex.matches(version).not()
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

val localProps = project.rootProject.file("local.properties")
val env: Map<String, String> = if (localProps.exists()) {
    // Auto import all of local.properties file as project wide properties
    val props = java.util.Properties()
    java.io.FileInputStream(localProps).bufferedReader().use {
        props.load(it)
    }
    mutableMapOf<String, String>().also { map ->
        props.forEach { entry ->
            map[entry.key.toString()] = entry.value.toString()
        }
    }
} else System.getenv()


fun ExtraPropertiesExtension.copy(key: String, map: Map<String, String>) {
    set(key, map.getOrDefault(key, ""))
}

extra.apply {
    copy("sonartypeStagingProfileId", env)
    copy("ossrhUsername", env)
    copy("ossrhPassword", env)
    copy("signing.keyId", env)
    copy("signing.password", env)
    copy("signing.secretKeyRingFile", env)
    copy("wakatimeRedirectScheme", env)
    copy("wakatimeRedirectHost", env)
    copy("wakatimeAppId", env)
    copy("wakatimeAppSecret", env)
}


nexusPublishing {
    repositories {
        sonatype {
            // Values previously read from local.properties
            val sonartypeStagingProfileId: String by extra
            val ossrhUsername: String by extra
            val ossrhPassword: String by extra

            stagingProfileId.set(sonartypeStagingProfileId)
            username.set(ossrhUsername)
            password.set(ossrhPassword)

            // Different nexus url requirements for signups made post 24th of february
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}