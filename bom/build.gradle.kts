plugins {
    id("java-platform")
}

javaPlatform {
    allowDependencies()
}

dependencies {
    constraints {
        api("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.31")

        //#region KotlinX Serialization
        api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")
        api("org.jetbrains.kotlinx:kotlinx-serialization-core:1.1.0")
        //#endregion

        val material_version: String by project
        api("com.google.android.material:material:$material_version")

        api("net.openid:appauth:0.8.0")

        val timber_version: String by project
        api("com.jakewharton.timber:timber:$timber_version")

        val okhttp3_version: String by project
        api("com.squareup.okhttp3:logging-interceptor:$okhttp3_version")

        //#region Retrofit2
        val retrofit2_version: String by project
        api("com.squareup.retrofit2:retrofit:$retrofit2_version")

        val retrofit2_kotlinx_serialization_converter_version: String by project
        api("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:$retrofit2_kotlinx_serialization_converter_version")
        //#endregion

        //#region AndroidX
        val androidx_core_version: String by project
        api("androidx.core:core-ktx:$androidx_core_version")

        val appcompat_version: String by project
        api("androidx.appcompat:appcompat:$appcompat_version")

        val androidx_constraintlayout_version: String by project
        api("androidx.constraintlayout:constraintlayout:$androidx_constraintlayout_version")

        val androidx_security_version: String by project
        api("androidx.security:security-crypto:$androidx_security_version")

        val androidx_swiperefreshlayout_version: String by project
        api("androidx.swiperefreshlayout:swiperefreshlayout:$androidx_swiperefreshlayout_version")
        //#endregion

        //#region AndroidX UI
        val androidx_activity_version: String by project
        api("androidx.activity:activity-ktx:$androidx_activity_version")

        val androidx_fragment_version: String by project
        api("androidx.fragment:fragment-ktx:$androidx_fragment_version")
        //#endregion

        //#region AndroidX Lifecycle
        val androidx_lifecycle_version: String by project
        api("androidx.lifecycle:lifecycle-livedata-ktx:$androidx_lifecycle_version")
        api("androidx.lifecycle:lifecycle-runtime-ktx:$androidx_lifecycle_version")
        api("androidx.lifecycle:lifecycle-viewmodel-ktx$androidx_lifecycle_version")
        //#endregion

        //#region Espresso
        val espresso_version: String by project
        api("androidx.test.espresso:espresso-core:$espresso_version")
        //#endregion

        //#region Kotest
        val kotest_version: String by project
        api("io.kotest:kotest-assertions-core:$kotest_version")
        api("io.kotest:kotest-runner-junit5:$kotest_version")
        api("io.kotest:kotest-property:$kotest_version")
        //#endregion

        //#region JUnit5
        val junit5_version: String by project
        api("org.junit.jupiter:junit-jupiter-api:$junit5_version")
        api("org.junit.jupiter:junit-jupiter-engine:$junit5_version")
        api("org.junit.jupiter:junit-jupiter-params:$junit5_version")
        api("org.junit.vintage:junit-vintage-engine:$junit5_version")
        //#endregion

        //#region Mannodermaus
        val mannodermaus_version: String by project
        api("de.mannodermaus.junit5:android-test-core:$mannodermaus_version")
        api("de.mannodermaus.junit5:android-test-runner:$mannodermaus_version")
        //#endregion

        //#region AndroidX Test
        val androidx_junit_ext: String by project
        api("androidx.test.ext:junit:$androidx_junit_ext")

        val androidx_test_runner: String by project
        api("androidx.test:runner:$androidx_test_runner")
        //#endregion
    }
}
