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
    implementation(fileTree(Pair("dir", "libs"), Pair("include", listOf("*.jar"))))
    implementation(project(":wakatimeclient"))

    implementation(Kotlin.stdlib.jdk8)

    implementation(AndroidX.appCompat)
    implementation(AndroidX.core)
    implementation(AndroidX.activityKtx)
    implementation(AndroidX.constraintLayout)
    implementation(AndroidX.lifecycle.extensions)
    implementation(AndroidX.lifecycle.liveDataKtx)
    implementation(AndroidX.lifecycle.runtimeKtx)
    implementation(AndroidX.lifecycle.viewModelKtx)

    implementation(Square.okHttp3.loggingInterceptor)

    androidTestImplementation(AndroidX.test.runner)
    androidTestImplementation(AndroidX.Test.Espresso.core)
}
