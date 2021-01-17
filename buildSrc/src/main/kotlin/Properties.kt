import org.gradle.api.JavaVersion

object Properties {

    object Common {

        const val minimumSdkVersion: Int = 16

        const val targetSdkVersion: Int = 30

        const val compileSdkVersion: Int = 30

        const val buildToolVersion: String = "29.0.2"

        val javaCompatibility: JavaVersion = JavaVersion.VERSION_1_8
    }
}