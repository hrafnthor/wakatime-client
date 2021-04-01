plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    compileSdkVersion(30)
    buildToolsVersion = "30.0.3"
    defaultConfig {
        applicationId = "is.hth.wakatimeclient.sample"
        minSdkVersion(23)
        targetSdkVersion(30)
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
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

dependencies {
    //#region Local
    implementation(enforcedPlatform(project(":bom")))
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
    implementation("androidx.security:security-crypto")
    implementation("androidx.constraintlayout:constraintlayout")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout")
    //#endregion

    //#region AndroidX Lifecycle
    implementation("androidx.lifecycle:lifecycle-livedata-ktx")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx")
    //#endregion
}