plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
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
    //  Languages related dependencies
    //
    implementation(Libs.kotlin_stdlib_jdk7)
    //
    //  Android Framework dependencies
    //
    implementation(Libs.appcompat)
    implementation(Libs.core_ktx)
    implementation(Libs.constraintlayout)
    //
    //  DI dependencies
    //
    implementation(Libs.dagger)
    implementation(Libs.dagger_android)
    implementation(Libs.dagger_android_support)
    kapt(Libs.dagger_compiler)
    //
    //
    //  Third party utility dependencies
    //
    implementation("net.openid:appauth:0.7.1")
    //
    //  Testing dependencies
    //
    testImplementation(Libs.junit)
    androidTestImplementation(Libs.androidx_test_runner)
    androidTestImplementation(Libs.espresso_core)
}
