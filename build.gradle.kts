buildscript {
    repositories {
        google()
        jcenter()

    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.5.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.41")
    }
}

plugins {
    // This plugin creates a separate module called 'buildSrc' containing all the
    // library references and version numbers, as well as query for library updates
    // Run 'buildSrcVersions' task for generation.
    // See here for more: https://github.com/jmfayard/buildSrcVersions
    id("de.fayard.buildSrcVersions") version "0.6.1"
}

allprojects {
    repositories {
        google()
        jcenter()

    }
}

tasks {
    val clean by registering(Delete::class) {
        delete(buildDir)
    }
}