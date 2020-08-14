plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
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
    //  Language related dependencies
    //
    implementation(Libs.kotlin_stdlib_jdk8)
    //
    //  Androidx library dependencies
    //
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.2.0")
    implementation(Libs.appcompat)
    implementation(Libs.constraintlayout)
    implementation(Libs.security_crypto)
    implementation(Libs.core_ktx)
    implementation(Libs.room_common)
    implementation(Libs.room_runtime)
    implementation("androidx.room:room-ktx:2.2.5")
    kapt(Libs.room_compiler)
    //
    //
    //  Third party utility dependencies
    //
    api(Libs.appauth)
    implementation("com.google.code.gson:gson:2.8.5")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
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
