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
    compileSdk = 30
    buildToolsVersion = "30.0.3"
    defaultConfig {
        applicationId = "is.hth.wakatimeclient.sample"
        minSdk = 23
        targetSdk = 30
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
            val redirectScheme = "<Your redirect scheme>"
            val redirectHost = "<Your redirect host>"
            manifestPlaceholders["appAuthRedirectScheme"] = redirectScheme
            manifestPlaceholders["appAuthRedirectHost"] = redirectHost
            setBuildConfigField("APPID", "<Your Wakatime generated app id")
            setBuildConfigField("SECRET", "<Your Wakatime generated secret>")
            setBuildConfigField("REDIRECT_URI", "$redirectScheme://$redirectHost")
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
