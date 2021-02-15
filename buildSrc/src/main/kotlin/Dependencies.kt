object Versions {
    // Language
    const val kotlinVersion = "1.4.30"

    // Plugin
    const val androidPluginVersion = "4.1.2"
    const val ktlintGradleVersion = "10.0.0"
    const val dokkaVersion = "0.9.17"
    const val bintrayGradlePluginVersion = "1.8.4"
    const val androidMavenGradlePluginVersion = "2.1"
    const val gradleVersionsPluginVersion = "0.36.0"

    // Android
    const val minSdkVersion = 19
    const val compileSdkVersion = 30
    const val targetSdkVersion = 30
    const val ndkVersion = "21.3.6528147"

    // Library
    const val androidxAppCompatVersion = "1.0.1"
    const val androidxAnnotationVersion = "1.1.0"
    const val androidxConstraintLayoutVersion = "1.1.3"
    const val androidxAnimatedVectorDrawable = "1.0.0"
    const val junitVersion = "4.13.2"
    const val robolectricVersion = "4.5.1"
    const val mockitoVersion = "3.7.7"
    const val mockitoKotlinVersion = "2.1.0"
}

object Libs {
    // Library
    const val androidxAppcompat =
        "androidx.appcompat:appcompat:${Versions.androidxAppCompatVersion}"
    const val androidxAnnotation =
        "androidx.annotation:annotation:${Versions.androidxAnnotationVersion}"
    const val androidxVectorDrawable =
        "androidx.vectordrawable:vectordrawable-animated:${Versions.androidxAnimatedVectorDrawable}"
    const val androidxConstraintLayout =
        "androidx.constraintlayout:constraintlayout:${Versions.androidxConstraintLayoutVersion}"
    const val junit = "junit:junit:${Versions.junitVersion}"
    const val robolectric = "org.robolectric:robolectric:${Versions.robolectricVersion}"
    const val mockitoInline = "org.mockito:mockito-inline:${Versions.mockitoVersion}"
    const val mockitoKotlin =
        "com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.mockitoKotlinVersion}"
}
