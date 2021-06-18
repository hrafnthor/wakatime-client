plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlinx-serialization")
    id("de.mannodermaus.android-junit5")
    id("maven-publish")
    id("org.jetbrains.dokka") version "1.4.32"
}

android {
    compileSdk = 30
    buildToolsVersion = "30.0.3"
    defaultConfig {
        minSdk = 16
        targetSdk = 30
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["runnerBuilder"] =
            "de.mannodermaus.junit5.AndroidJUnit5Builder"

        // Default values given for the benefit of AppAuth to shut
        // up the manifest merger during tests
        manifestPlaceholders["appAuthRedirectScheme"] = ""
        manifestPlaceholders["appAuthRedirectHost"] = ""

    }

    buildTypes {
        getByName("release") {
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
        // Turn on strict compiler flags to force a stance on library visibility
        freeCompilerArgs = freeCompilerArgs + "-Xexplicit-api=strict"
    }
}

dependencies {
    implementation(catalog.kotlin.stdlib.jdk8)

    //#region KotlinX Serialization
    implementation(catalog.kotlinx.serialization.json)
    implementation(catalog.kotlinx.serialization.core)
    //#endregion

    //#region Androidx
    implementation(catalog.androidx.core.ktx)
    implementation(catalog.androidx.appcompat)
    implementation(catalog.androidx.lifecycle.runtime.ktx)
    //#endregion

    api(catalog.appauth)

    implementation(catalog.square.retrofit2)
    implementation(catalog.jakewharton.retrofit2.serialization)
    implementation(catalog.jakewharton.timber)

    //#region Kotest
    testImplementation(catalog.kotest.junit5.runner)
    testImplementation(catalog.kotest.assertions.core)
    testImplementation(catalog.kotest.assertions.json)
    //#endregion

    //#region JVM test
    testImplementation(catalog.kotlin.reflect)

    testImplementation(catalog.junit5.api)
    testRuntimeOnly(catalog.junit5.engine)

    testImplementation(catalog.square.okhttp3.mockwebserver)
    //#endregion

    //#region Android Test
    androidTestImplementation(catalog.androidx.test.runner)
    androidTestImplementation(catalog.androidx.test.extensions.junit)
    androidTestImplementation(catalog.junit5.api)

    androidTestImplementation(catalog.mannodermaus.junit5.android.core)
    androidTestRuntimeOnly(catalog.mannodermaus.junit5.android.runner)
    //#endregion
}

tasks {
    val htmlDocPath = "$buildDir/docs/html"
    dokkaHtml.configure {
        outputDirectory.set(File(htmlDocPath))
    }
    register("androidHtmlDocJar", Jar::class) {
        archiveClassifier.set("html")
        from(htmlDocPath)
        dependsOn(dokkaHtml)
    }

    register("androidSourceJar", Jar::class) {
        archiveClassifier.set("sources")
        from(android.sourceSets.getByName("main").java.srcDirs)
    }
}