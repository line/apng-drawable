object Versions {
    // Language
    const val kotlinVersion = "1.3.11"

    // Plugin
    const val androidPluginVersion = "3.3.0-rc03"
    const val ktlintGradleVersion = "6.3.0"
    const val dokkaVersion = "0.9.17"
    const val bintrayGradlePluginVersion = "1.8.4"
    const val androidMavenGradlePluginVersion = "2.1"
    const val gradleVersionsPluginVersion = "0.20.0"

    // Android
    const val minSdkVersion = 19
    const val compileSdkVersion = 28
    const val targetSdkVersion = 28

    // Library
    const val androidxVersion = "1.0.1"
    const val androidxConstraintLayoutVersion = "1.1.3"
    const val androidxAnimatedVectorDrawable = "1.0.0"
    const val junitVersion = "4.12"
    const val robolectricVersion = "4.0.2"
    const val mockitoVersion = "2.23.4"
    const val mockitoKotlinVersion = "2.0.0"
}

object Libs {
    // Plugin
    val androidGradlePlugin = "com.android.tools.build:gradle:${Versions.androidPluginVersion}"
    val dokkaAndroidGradlePlugin =
        "org.jetbrains.dokka:dokka-android-gradle-plugin:${Versions.dokkaVersion}"
    val ktlintGradlePlugin = "org.jlleitschuh.gradle:ktlint-gradle:${Versions.ktlintGradleVersion}"
    val bintrayGradlePlugin =
        "com.jfrog.bintray.gradle:gradle-bintray-plugin:${Versions.bintrayGradlePluginVersion}"
    val androidManvenGradlePlugin =
        "com.github.dcendents:android-maven-gradle-plugin:${Versions.androidMavenGradlePluginVersion}"
    val gradleVersionsPlugin =
        "com.github.ben-manes:gradle-versions-plugin:${Versions.gradleVersionsPluginVersion}"

    // Library
    val androidxAppcompat = "androidx.appcompat:appcompat:${Versions.androidxVersion}"
    val androidxAnnotation = "androidx.annotation:annotation:${Versions.androidxVersion}"
    val androidxVectorDrawable =
        "androidx.vectordrawable:vectordrawable-animated:${Versions.androidxAnimatedVectorDrawable}"
    val androidxConstraintLayout =
        "androidx.constraintlayout:constraintlayout:${Versions.androidxConstraintLayoutVersion}"
    val junit = "junit:junit:${Versions.junitVersion}"
    val robolectric = "org.robolectric:robolectric:${Versions.robolectricVersion}"
    val mockitoInline = "org.mockito:mockito-inline:${Versions.mockitoVersion}"
    val mockitoKotlin =
        "com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.mockitoKotlinVersion}"
}
