plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
}

android {
    compileSdkVersion(Properties.Common.compileSdkVersion)

    defaultConfig {
        applicationId = "is.hth.wakatimeclient.sample"
        minSdkVersion(Properties.Common.minimumSdkVersion)
        targetSdkVersion(Properties.Common.targetSdkVersion)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        dataBinding = true
    }

    buildTypes {
        getByName("debug") {
            // The defined OAuth redirect scheme as defined inside Wakatime's app dashboard
            manifestPlaceholders["appAuthRedirectScheme"] = "vakta://grant-callback"
        }
        getByName("release") {

        }
    }
    compileOptions {
        sourceCompatibility = Properties.Common.javaCompatibility
        targetCompatibility = Properties.Common.javaCompatibility
    }
    kotlinOptions {
        jvmTarget = Properties.Common.javaCompatibility.toString()
    }
    buildToolsVersion = Properties.Common.buildToolVersion
}

dependencies {
    //#region Local
    implementation(enforcedPlatform(project(":bom")))
    implementation(fileTree(Pair("dir", "libs"), Pair("include", listOf("*.jar"))))
    implementation(project(":wakatimeclient"))
    //#endregion

    //#region Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    //#endregion

    //#region Androidx
    implementation("androidx.core:core-ktx")
    implementation("androidx.appcompat:appcompat")
    implementation("androidx.activity:activity-ktx")
    implementation("androidx.fragment:fragment-ktx")
    implementation("androidx.constraintlayout:constraintlayout")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout")
    //#endregion

    //#region AndroidX Lifecycle
    implementation("androidx.lifecycle:lifecycle-livedata-ktx")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx")
    //#endregion
}
