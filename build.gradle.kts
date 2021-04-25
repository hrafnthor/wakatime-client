buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.1.3")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.31")
        classpath("de.mannodermaus.gradle.plugins:android-junit5:1.7.1.1")
    }
}

plugins {
    // Used for dependency update checking
    id("com.github.ben-manes.versions") version "0.38.0"
    // Native Kotlin Serialization
    kotlin("plugin.serialization") version "1.4.31"
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks {
    val clean by registering(Delete::class) {
        delete(buildDir)
    }
}