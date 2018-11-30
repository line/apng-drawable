object Versions {
    // Language
    const val kotlinVersion = "1.3.0"

    // Plugin
    const val androidPluginVersion = "3.3.0-rc01"
    const val ktlintGradleVersion = "6.3.0"
    const val dokkaVersion = "0.9.17"

    // Android
    const val minSdkVersion = 19
    const val compileSdkVersion = 28
    const val targetSdkVersion = 28

    // Library
    const val androidxVersion = "1.0.0"
    const val androidxConstraintLayoutVersion = "1.1.3"
    const val junitVersion = "4.12"
    const val robolectricVersion = "3.8"
    const val mockitoVersion = "2.23.4"
    const val mockitoKotlinVersion = "2.0.0"
}

object Libs {
    val androidxAppcompat = "androidx.appcompat:appcompat:${Versions.androidxVersion}"
    val androidxAnnotation = "androidx.annotation:annotation:${Versions.androidxVersion}"
    val androidxConstraintLayout =
        "androidx.constraintlayout:constraintlayout:${Versions.androidxConstraintLayoutVersion}"
    val junit = "junit:junit:${Versions.junitVersion}"
    val robolectric = "org.robolectric:robolectric:${Versions.robolectricVersion}"
    val mockitoInline = "org.mockito:mockito-inline:${Versions.mockitoVersion}"
    val mockitoKotlin =
        "com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.mockitoKotlinVersion}"
}
