import org.gradle.api.JavaVersion

object Properties {

    object Common {

        const val minimumSdkVersion: Int = 15

        const val targetSdkVersion: Int = 29

        const val compileSdkVersion: Int = 29

        const val buildToolVersion: String = "29.0.2"

        val javaCompatibility: JavaVersion = JavaVersion.VERSION_1_8
    }
}