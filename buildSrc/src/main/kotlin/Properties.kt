import org.gradle.api.JavaVersion

object Properties {

    object Common {

        const val minimumSdkVersion: Int = 16

        const val targetSdkVersion: Int = 29

        const val compileSdkVersion: Int = 29

        const val buildToolVersion: String = "29.0.2"

        val javaCompatibility: JavaVersion = JavaVersion.VERSION_1_8
    }

    object Sample {
        /**
         * The defined OAuth redirect scheme as defined inside Wakatime's app dashboard
         */
        const val redirectUri: String = "\"vakta://grant-callback\""

    }
}