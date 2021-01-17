plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
    id("kotlinx-serialization")
    id("de.mannodermaus.android-junit5")
}

android {
    compileSdkVersion(Properties.Common.compileSdkVersion)
    defaultConfig {
        minSdkVersion(Properties.Common.minimumSdkVersion)
        targetSdkVersion(Properties.Common.targetSdkVersion)
        versionCode = 1
        versionName= "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("debug") {

        }
        getByName("release") {
//            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
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
    //
    //  Local dependencies
    //
    implementation(fileTree(Pair("dir", "libs"), Pair("include", listOf("*.jar"))))
    //
    //  Language and compiler related dependencies
    //
    implementation(Kotlin.stdlib.jdk8)
    implementation(KotlinX.serialization.core)
    //
    //  Androidx library dependencies
    //
    implementation(AndroidX.lifecycle.liveDataKtx)
    implementation(AndroidX.appCompat)
    implementation(AndroidX.constraintLayout)
    implementation(AndroidX.security.crypto)
    implementation(AndroidX.core.ktx)
    //
    //
    //  Third party utility dependencies
    //
    api("net.openid:appauth:0.7.1")
    implementation(Square.retrofit2.retrofit)
    implementation(JakeWharton.retrofit2.converter.kotlinxSerialization)
    implementation(JakeWharton.timber)
    //
    //  Testing dependencies
    //
    testImplementation(Testing.JunitJupiter.api)
    testRuntimeOnly(Testing.JunitJupiter.engine)
    testImplementation(Testing.JunitJupiter.params)
    testImplementation(Testing.MockK)
    testImplementation("org.assertj:assertj-core:3.14.0")

    //
    //  Instrumented testing dependencies
    //
    androidTestImplementation(AndroidX.Test.runner)
    androidTestImplementation(AndroidX.Test.Espresso.core)
}
