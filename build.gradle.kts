buildscript {
    repositories {
        google()
        jcenter()

    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.0.0-alpha04")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.50")
    }
}

//plugins {
//    // This plugin creates a separate module called 'buildSrc' containing all the
//    // library references and version numbers, as well as query for library updates
//    // Run 'buildSrcVersions' task for generation.
//    // See here for more: https://github.com/jmfayard/buildSrcVersions
//    id("de.fayard.buildSrcVersions") version "0.6.1"
//}

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