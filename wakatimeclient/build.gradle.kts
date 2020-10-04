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
}

dependencies {
    //
    //  Local dependencies
    //
    implementation(fileTree(Pair("dir", "libs"), Pair("include", listOf("*.jar"))))
    //
    //  Language and compiler related dependencies
    //
    implementation(Libs.kotlin_stdlib_jdk8)
    implementation(Libs.kotlinx_serialization_core)
    //
    //  Androidx library dependencies
    //
    implementation(Libs.lifecycle_livedata_ktx)
    implementation(Libs.appcompat)
    implementation(Libs.constraintlayout)
    implementation(Libs.security_crypto)
    implementation(Libs.core_ktx)
    implementation(Libs.room_common)
    implementation(Libs.room_runtime)
    implementation(Libs.room_ktx)
    kapt(Libs.room_compiler)
    //
    //
    //  Third party utility dependencies
    //
    api(Libs.appauth)
    implementation(Libs.gson)
    implementation(Libs.retrofit)
    implementation(Libs.converter_gson)
    implementation(Libs.retrofit2_kotlinx_serialization_converter)
    implementation(Libs.timber)
    //
    //  Testing dependencies
    //
    testImplementation(Libs.junit_jupiter_api)
    testRuntimeOnly(Libs.junit_jupiter_engine)
    testImplementation(Libs.junit_jupiter_params)
    testImplementation(Libs.mockk)
    testImplementation(Libs.assertj_core)
    //
    //  Instrumented testing dependencies
    //
    androidTestImplementation(Libs.androidx_test_runner)
    androidTestImplementation(Libs.espresso_core)
}
