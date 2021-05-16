plugins {
    id("java-platform")
}

javaPlatform {
    allowDependencies()
}

dependencies {
    constraints {
        val kotlinVersion: String by project
        api("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")

        //#region KotlinX Serialization
        val kotlinxSerializationVersion: String by project
        api("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
        api("org.jetbrains.kotlinx:kotlinx-serialization-core:$kotlinxSerializationVersion")
        //#endregion

        val materialVersion: String by project
        api("com.google.android.material:material:$materialVersion")

        val appauthVersion: String by project
        api("net.openid:appauth:$appauthVersion")

        val timberVersion: String by project
        api("com.jakewharton.timber:timber:$timberVersion")

        //#region OKHttp3
        val okhttp3Version: String by project
        api("com.squareup.okhttp3:logging-interceptor:$okhttp3Version")
        //#endregion

        //#region Retrofit2
        val retrofit2Version: String by project
        api("com.squareup.retrofit2:retrofit:$retrofit2Version")

        val retrofit2SerializationConverterVersion: String by project
        api("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:$retrofit2SerializationConverterVersion")
        //#endregion

        //#region AndroidX
        val androidxCoreVersion: String by project
        api("androidx.core:core-ktx:$androidxCoreVersion")

        val appcompatVersion: String by project
        api("androidx.appcompat:appcompat:$appcompatVersion")

        val androidxConstraintlayoutVersion: String by project
        api("androidx.constraintlayout:constraintlayout:$androidxConstraintlayoutVersion")

        val androidxSecurityVersion: String by project
        api("androidx.security:security-crypto:$androidxSecurityVersion")

        val androidxSwiperefreshlayoutVersion: String by project
        api("androidx.swiperefreshlayout:swiperefreshlayout:$androidxSwiperefreshlayoutVersion")
        //#endregion

        //#region AndroidX UI
        val androidxActivityVersion: String by project
        api("androidx.activity:activity-ktx:$androidxActivityVersion")
        //#endregion

        //#region AndroidX Lifecycle
        val androidxLifecycleVersion: String by project
        api("androidx.lifecycle:lifecycle-livedata-ktx:$androidxLifecycleVersion")
        api("androidx.lifecycle:lifecycle-runtime-ktx:$androidxLifecycleVersion")
        api("androidx.lifecycle:lifecycle-viewmodel-ktx$androidxLifecycleVersion")
        //#endregion

        //#region Kotest
        val kotestVersion: String by project
        api("io.kotest:kotest-assertions-core:$kotestVersion")
        api("io.kotest:kotest-runner-junit5:$kotestVersion")
        api("io.kotest:kotest-property:$kotestVersion")
        //#endregion

        //#region JUnit5
        val junit5Version: String by project
        api("org.junit.jupiter:junit-jupiter-api:$junit5Version")
        api("org.junit.jupiter:junit-jupiter-engine:$junit5Version")
        api("org.junit.jupiter:junit-jupiter-params:$junit5Version")
        api("org.junit.vintage:junit-vintage-engine:$junit5Version")
        //#endregion

        //#region Mannodermaus
        val mannodermausLibraryVersion: String by project
        api("de.mannodermaus.junit5:android-test-core:$mannodermausLibraryVersion")
        api("de.mannodermaus.junit5:android-test-runner:$mannodermausLibraryVersion")
        //#endregion

        //#region AndroidX Test
        val androidxJunitExtVersion: String by project
        api("androidx.test.ext:junit:$androidxJunitExtVersion")

        val androidxTestRunner: String by project
        api("androidx.test:runner:$androidxTestRunner")
        //#endregion
    }
}
