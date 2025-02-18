import org.gradle.api.JavaVersion

object Config {

    const val SDK_VERSION = 35
    const val MIN_SDK = 24
    val JAVA_VERSION = JavaVersion.VERSION_17
    const val COMPOSE_VERSION = "1.5.1"

    object App {
        const val VERSION_CODE = 1
        const val VERSION_NAME = "0.0.1"
    }

}