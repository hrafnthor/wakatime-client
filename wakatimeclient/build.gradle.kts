plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlinx-serialization")
    id("de.mannodermaus.android-junit5")
}

android {
    compileSdk = 30
    buildToolsVersion = "30.0.3"
    defaultConfig {
        minSdk = 16
        targetSdk = 30
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["runnerBuilder"] = "de.mannodermaus.junit5.AndroidJUnit5Builder"

        // Default values given for the benefit of AppAuth to shut
        // up the manifest merger during tests
        manifestPlaceholders["appAuthRedirectScheme"] = ""
        manifestPlaceholders["appAuthRedirectHost"] = ""

    }
    buildTypes {
        getByName("debug") {

        }
        getByName("release") {
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
        freeCompilerArgs = freeCompilerArgs + "-Xexplicit-api=strict"
    }
    packagingOptions {
        exclude("META-INF/LICENSE*")
    }
}


dependencies {
    //#region Local
    implementation(enforcedPlatform(project(":bom")))
    //#endregion

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    //#region KotlinX Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core")
    //#endregion

    //#region Androidx
    implementation("androidx.core:core-ktx")
    implementation("androidx.appcompat:appcompat")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx")
    //#endregion

    api("net.openid:appauth")

    implementation("com.squareup.retrofit2:retrofit")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter")
    implementation("com.jakewharton.timber:timber")

    //#region Kotest
    testImplementation("io.kotest:kotest-runner-junit5")
    testImplementation("io.kotest:kotest-assertions-core")
    //#endregion

    //#region Test
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    //#endregion

    //#region Android Test
    androidTestImplementation("androidx.test:runner")
    androidTestImplementation("androidx.test.ext:junit")
    androidTestImplementation("org.junit.jupiter:junit-jupiter-api")

    androidTestImplementation("de.mannodermaus.junit5:android-test-core")
    androidTestRuntimeOnly("de.mannodermaus.junit5:android-test-runner")
    //#endregion
}
