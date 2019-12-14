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
    buildTypes {
        getByName("debug") {

        }
        getByName("release") {
            //            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    compileOptions {
        sourceCompatibility = Properties.Common.javaCompatibility
        targetCompatibility = Properties.Common.javaCompatibility
    }
    buildToolsVersion = Properties.Common.buildToolVersion
}

dependencies {
    implementation(fileTree(Pair("dir", "libs"), Pair("include", listOf("*.jar"))))
    implementation(Libs.kotlin_stdlib_jdk7)
    implementation(Libs.appcompat)
    implementation(Libs.core_ktx)
    implementation(Libs.constraintlayout)
    testImplementation(Libs.junit)
    androidTestImplementation(Libs.androidx_test_runner)
    androidTestImplementation(Libs.espresso_core)
}
