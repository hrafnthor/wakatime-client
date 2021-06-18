include(":sample", ":wakatimeclient")
// The dependency resolution management feature used below is
// currently an incubating feature and needs to be opted into
enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
    versionCatalogs {
        create("catalog") {
            //#region kotlin
            val kotlinVersion: String by settings
            val kotlin = version("kotlin", kotlinVersion)
            alias("kotlin-stdlib-jdk8")
                .to("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
                .versionRef(kotlin)
            alias("kotlin-reflect")
                .to("org.jetbrains.kotlin", "kotlin-reflect")
                .versionRef(kotlin)
            alias("kotlin-gradle")
                .to("org.jetbrains.kotlin", "kotlin-gradle-plugin")
                .versionRef(kotlin)

            val kotlinxSerializationVersion: String by settings
            val kotlinSerialization = version("kotlinx-serialization", kotlinxSerializationVersion)
            alias("kotlinx-serialization-json")
                .to("org.jetbrains.kotlinx", "kotlinx-serialization-json")
                .versionRef(kotlinSerialization)
            alias("kotlinx-serialization-core")
                .to("org.jetbrains.kotlinx", "kotlinx-serialization-core")
                .versionRef(kotlinSerialization)
            //#endregion

            val materialVersion: String by settings
            alias("google-material-theme")
                .to("com.google.android.material","material")
                .version(materialVersion)

            val appauthVersion: String by settings
            alias("appauth")
                .to("net.openid", "appauth")
                .version(appauthVersion)

            val timberVersion: String by settings
            alias("jakewharton-timber")
                .to("com.jakewharton.timber", "timber")
                .version(timberVersion)

            //#region Network
            val okhttp3Version: String by settings
            alias("square-okhttp3-logging")
                .to("com.squareup.okhttp3", "logging-interceptor")
                .version(okhttp3Version)

            val retrofit2MockWebServerVersion: String by settings
            alias("square-okhttp3-mockwebserver")
                .to("com.squareup.okhttp3","mockwebserver")
                .version(retrofit2MockWebServerVersion)

            val retrofit2Version: String by settings
            alias("square-retrofit2")
                .to("com.squareup.retrofit2", "retrofit")
                .version(retrofit2Version)

            val retrofit2SerializationConverterVersion: String by settings
            alias("jakewharton-retrofit2-serialization")
                .to("com.jakewharton.retrofit","retrofit2-kotlinx-serialization-converter")
                .version(retrofit2SerializationConverterVersion)

            //#endregion

            //#region AndroidX
            val androidxCoreVersion: String by settings
            alias("androidx-core-ktx")
                .to("androidx.core", "core-ktx")
                .version(androidxCoreVersion)

            val appcompatVersion: String by settings
            alias("androidx-appcompat")
                .to("androidx.appcompat", "appcompat")
                .version(appcompatVersion)

            val androidxConstraintlayoutVersion: String by settings
            alias("androidx-constraintlayout")
                .to("androidx.constraintlayout", "constraintlayout")
                .version(androidxConstraintlayoutVersion)

            val androidxSecurityVersion: String by settings
            alias("androidx-security-crypto")
                .to("androidx.security", "security-crypto")
                .version(androidxSecurityVersion)

            val androidxSwiperefreshlayoutVersion: String by settings
            alias("androidx-swiperefreshlayout")
                .to("androidx.swiperefreshlayout","swiperefreshlayout")
                .version(androidxSwiperefreshlayoutVersion)

            val androidxActivityVersion: String by settings
            alias("androidx-activity-ktx")
                .to("androidx.activity","activity-ktx")
                .version(androidxActivityVersion)
            // fragment (not -ktx) required for the use of ActivityResultLauncher
            val androidxFragmentVersion: String by settings
            alias("androidx-fragment-ktx")
                .to("androidx.fragment", "fragment")
                .version(androidxFragmentVersion)

            val androidxLifecycleVersion: String by settings
            val androidxLifecycle = version("androidx-lifecycle", androidxLifecycleVersion)
            alias("androidx-lifecycle-livedata-ktx")
                .to("androidx.lifecycle", "lifecycle-livedata-ktx")
                .versionRef(androidxLifecycle)
            alias("androidx-lifecycle-runtime-ktx")
                .to("androidx.lifecycle", "lifecycle-runtime-ktx")
                .versionRef(androidxLifecycle)
            alias("androidx-lifecycle-viewmodel-ktx")
                .to("androidx.lifecycle", "lifecycle-viewmodel-ktx")
                .versionRef(androidxLifecycle)
            //#endregion

            //#region Kotest
            val kotestVersion: String by settings
            val kotest = version("kotest", kotestVersion)
            alias("kotest-assertions-core")
                .to("io.kotest", "kotest-assertions-core")
                .versionRef(kotest)
            alias("kotest-assertions-json")
                .to("io.kotest", "kotest-assertions-json")
                .versionRef(kotest)
            alias("kotest-junit5-runner")
                .to("io.kotest", "kotest-runner-junit5")
                .versionRef(kotest)
            alias("kotest-property")
                .to("io.kotest", "kotest-property")
                .versionRef(kotest)
            //#endregion

            //#region JUnit5
            val junit5Version: String by settings
            val junit5 = version("junit5", junit5Version)
            alias("junit5-api")
                .to("org.junit.jupiter", "junit-jupiter-api")
                .versionRef(junit5)
            alias("junit5-engine")
                .to("org.junit.jupiter", "junit-jupiter-engine")
                .versionRef(junit5)
            alias("junit5-params")
                .to("org.junit.jupiter", "junit-jupiter-params")
                .versionRef(junit5)
            alias("junit-engine")
                .to("org.junit.vintage", "junit-vintage-engine")
                .versionRef(junit5)
            //#endregion

            //#region Mannodermaus
            val mannodermausLibraryVersion: String by settings
            val mannodermausJunit5 = version("mannodermaus-junit5", mannodermausLibraryVersion)
            alias("mannodermaus-junit5-android-core")
                .to("de.mannodermaus.junit5", "android-test-core")
                .versionRef(mannodermausJunit5)
            alias("mannodermaus-junit5-android-runner")
                .to("de.mannodermaus.junit5", "android-test-runner")
                .versionRef(mannodermausJunit5)

            val mannodermausPluginVersion: String by settings
            alias("mannodermaus-plugin")
                .to("de.mannodermaus.gradle.plugins", "android-junit5")
                .version(mannodermausPluginVersion)
            //#endregion

            //#region AndroidX Test
            val androidxJunitExtVersion: String by settings
            alias("androidx-test-extensions-junit")
                .to("androidx.test.ext", "junit")
                .version(androidxJunitExtVersion)

            val androidxTestRunner: String by settings
            alias("androidx-test-runner")
                .to("androidx.test", "runner")
                .version(androidxTestRunner)

            val androidGradlePluginVersion: String by settings
            val androidGradle = version("android-gradle", androidGradlePluginVersion)
            alias("android-gradle")
                .to("com.android.tools.build", "gradle")
                .versionRef(androidGradle)
        }
    }
}
