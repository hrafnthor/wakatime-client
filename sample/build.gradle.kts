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
    implementation("androidx.security:security-crypto")
    implementation("androidx.constraintlayout:constraintlayout")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout")
    //#endregion

    //#region AndroidX Lifecycle
    implementation("androidx.lifecycle:lifecycle-livedata-ktx")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx")
    //#endregion

    implementation("com.squareup.okhttp3:logging-interceptor")
}