import org.gradle.api.JavaVersion

object Configs {
    const val minSdk = 21
    const val compileSdk = 34
    const val targetSdk = 34
    const val versionCode = 12000
    const val versionName = "1.2.0"
    const val namespace = "com.tangping.androidpractice"
    const val applicationId = "com.tangping.androidpractice"
    const val jvmTarget = "17"
    val sourceCompatibility = JavaVersion.VERSION_17
    val targetCompatibility = JavaVersion.VERSION_17
}