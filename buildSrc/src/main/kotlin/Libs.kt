import kotlin.String

/**
 * Generated by https://github.com/jmfayard/buildSrcVersions
 *
 * Update this file with
 *   `$ ./gradlew buildSrcVersions`
 */
object Libs {
    /**
     * https://kotlinlang.org/
     */
    const val kotlin_android_extensions: String =
            "org.jetbrains.kotlin:kotlin-android-extensions:" + Versions.org_jetbrains_kotlin

    /**
     * https://kotlinlang.org/
     */
    const val kotlin_android_extensions_runtime: String =
            "org.jetbrains.kotlin:kotlin-android-extensions-runtime:" +
            Versions.org_jetbrains_kotlin

    /**
     * https://kotlinlang.org/
     */
    const val kotlin_annotation_processing_gradle: String =
            "org.jetbrains.kotlin:kotlin-annotation-processing-gradle:" +
            Versions.org_jetbrains_kotlin

    /**
     * https://kotlinlang.org/
     */
    const val kotlin_gradle_plugin: String = "org.jetbrains.kotlin:kotlin-gradle-plugin:" +
            Versions.org_jetbrains_kotlin

    /**
     * https://kotlinlang.org/
     */
    const val kotlin_stdlib_jdk7: String = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:" +
            Versions.org_jetbrains_kotlin

    /**
     * https://kotlinlang.org/
     */
    const val kotlin_stdlib_jdk8: String = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:" +
            Versions.org_jetbrains_kotlin

    /**
     * https://junit.org/junit5/
     */
    const val junit_jupiter_api: String = "org.junit.jupiter:junit-jupiter-api:" +
            Versions.org_junit_jupiter

    /**
     * https://junit.org/junit5/
     */
    const val junit_jupiter_engine: String = "org.junit.jupiter:junit-jupiter-engine:" +
            Versions.org_junit_jupiter

    /**
     * https://junit.org/junit5/
     */
    const val junit_jupiter_params: String = "org.junit.jupiter:junit-jupiter-params:" +
            Versions.org_junit_jupiter

    /**
     * https://developer.android.com/studio
     */
    const val com_android_tools_build_gradle: String = "com.android.tools.build:gradle:" +
            Versions.com_android_tools_build_gradle

    /**
     * https://developer.android.com/testing
     */
    const val androidx_test_runner: String = "androidx.test:runner:" + Versions.androidx_test_runner

    /**
     * https://developer.android.com/topic/libraries/architecture/index.html */
    const val room_common: String = "androidx.room:room-common:" + Versions.androidx_room

    /**
     * https://developer.android.com/topic/libraries/architecture/index.html */
    const val room_compiler: String = "androidx.room:room-compiler:" + Versions.androidx_room

    /**
     * https://developer.android.com/topic/libraries/architecture/index.html */
    const val room_runtime: String = "androidx.room:room-runtime:" + Versions.androidx_room



    const val de_fayard_buildsrcversions_gradle_plugin: String =
            "de.fayard.buildSrcVersions:de.fayard.buildSrcVersions.gradle.plugin:" +
            Versions.de_fayard_buildsrcversions_gradle_plugin

    /**
     * http://tools.android.com
     */
    const val constraintlayout: String = "androidx.constraintlayout:constraintlayout:" +
            Versions.constraintlayout

    const val security_crypto: String = "androidx.security:security-crypto:" +
            Versions.security_crypto

    /**
     * https://github.com/mannodermaus/android-junit5
     */
    const val android_junit5: String = "de.mannodermaus.gradle.plugins:android-junit5:" +
            Versions.android_junit5

    /**
     * https://developer.android.com/testing
     */
    const val espresso_core: String = "androidx.test.espresso:espresso-core:" +
            Versions.espresso_core

    /**
     * http://assertj.org
     */
    const val assertj_core: String = "org.assertj:assertj-core:" + Versions.assertj_core

    /**
     * https://developer.android.com/studio
     */
    const val lint_gradle: String = "com.android.tools.lint:lint-gradle:" + Versions.lint_gradle

    /**
     * https://developer.android.com/jetpack/androidx
     */
    const val appcompat: String = "androidx.appcompat:appcompat:" + Versions.appcompat

    /**
     * https://developer.android.com/jetpack/androidx
     */
    const val core_ktx: String = "androidx.core:core-ktx:" + Versions.core_ktx

    /**
     * https://github.com/openid/AppAuth-Android
     */
    const val appauth: String = "net.openid:appauth:" + Versions.appauth

    /**
     * https://github.com/JakeWharton/timber
     */
    const val timber: String = "com.jakewharton.timber:timber:" + Versions.timber

    /**
     * https://developer.android.com/studio
     */
    const val aapt2: String = "com.android.tools.build:aapt2:" + Versions.aapt2

    /**
     * http://mockk.io
     */
    const val mockk: String = "io.mockk:mockk:" + Versions.mockk
}