buildscript {
    repositories {
        google()
        jcenter()

    }
    dependencies {
        classpath(Libs.com_android_tools_build_gradle)
        classpath(Libs.kotlin_gradle_plugin)
        classpath(Libs.android_junit5)
    }
}

plugins {
    // This plugin creates a separate module called 'buildSrc' containing all the
    // library references and version numbers, as well as query for library updates
    // Run 'buildSrcVersions' task for generation.
    // See here for more: https://github.com/jmfayard/buildSrcVersions
    buildSrcVersions
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