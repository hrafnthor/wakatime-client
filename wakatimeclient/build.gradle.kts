plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
    id("kotlinx-serialization")
}

android {
    compileSdkVersion(Properties.Common.compileSdkVersion)
    defaultConfig {
        minSdkVersion(Properties.Common.minimumSdkVersion)
        targetSdkVersion(Properties.Common.targetSdkVersion)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("debug") {

        }
        getByName("release") {
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildToolsVersion = Properties.Common.buildToolVersion
    compileOptions {
        sourceCompatibility = Properties.Common.javaCompatibility
        targetCompatibility = Properties.Common.javaCompatibility
    }

    kotlinOptions {
        jvmTarget = Properties.Common.javaCompatibility.toString()
    }
}

dependencies {
    //#region Local
    implementation(enforcedPlatform(project(":bom")))
    implementation(fileTree(Pair("dir", "libs"), Pair("include", listOf("*.jar"))))
    //#endregion

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    //#region KotlinX Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core")
    //#endregion

    //#region Androidx
    implementation("androidx.core:core-ktx")
    implementation("androidx.appcompat:appcompat")
    implementation("androidx.security:security-crypto")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx")
    //#endregion

    api("net.openid:appauth")

    implementation("com.squareup.retrofit2:retrofit")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter")
    implementation("com.squareup.okhttp3:logging-interceptor")
    implementation("com.jakewharton.timber:timber")

    //#region Kotest
    testImplementation("io.kotest:kotest-runner-junit5")
    //#endregion

    //#region JUnit
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    androidTestImplementation("de.mannodermaus.junit5:android-test-core")
    androidTestRuntimeOnly("de.mannodermaus.junit5:android-test-runner")
    //#endregion

    //#region Android Test
    androidTestImplementation("androidx.test:runner")
    androidTestImplementation("androidx.test.ext:junit")
    androidTestImplementation("androidx.test.espresso:espresso-core")
    //#endregion
}
