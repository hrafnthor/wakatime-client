import com.android.build.api.dsl.ApplicationBuildType

fun ApplicationBuildType.setBuildConfigField(key: String, value: String) {
    buildConfigField("String", key, "\"$value\"")
}

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    compileSdk = 31
    buildToolsVersion = "30.0.3"
    defaultConfig {
        applicationId = "is.hth.wakatimeclient.sample"
        minSdk = 23
        targetSdk = 31
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
            // These values should be placed in the projects local.properties file
            val wakatimeRedirectScheme: String by rootProject.extra
            val wakatimeRedirectHost: String by rootProject.extra
            manifestPlaceholders["appAuthRedirectScheme"] = wakatimeRedirectScheme
            manifestPlaceholders["appAuthRedirectHost"] = wakatimeRedirectHost

            // This is not a recommended way to handle sensitive information, and is only
            // done like this here for sample purposes! Deliver sensitive values to the client
            // via a secure backend or Firebase using for instance SafetyNet to verify the client
            // and environment.
            val wakatimeAppId: String by rootProject.extra
            val wakatimeAppSecret: String by rootProject.extra
            setBuildConfigField("APPID", wakatimeAppId)
            setBuildConfigField("SECRET", wakatimeAppSecret)
            setBuildConfigField("REDIRECT_URI", "$wakatimeRedirectScheme://$wakatimeRedirectHost")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}

dependencies {
    //#region Local
    implementation(project(":wakatimeclient"))
    //#endregion

    //#region Kotlin
    implementation(catalog.kotlin.stdlib.jdk8)
    //#endregion

    //#region Androidx
    implementation(catalog.androidx.core.ktx)
    implementation(catalog.androidx.appcompat)
    implementation(catalog.androidx.activity.ktx)
    implementation(catalog.androidx.security.crypto)
    implementation(catalog.androidx.constraintlayout)
    implementation(catalog.androidx.swiperefreshlayout)
    //#endregion

    //#region AndroidX Lifecycle
    implementation(catalog.androidx.lifecycle.livedata.ktx)
    implementation(catalog.androidx.lifecycle.runtime.ktx)
    implementation(catalog.androidx.lifecycle.viewmodel.ktx)
    //#endregion

    implementation(catalog.square.okhttp3.logging)
}