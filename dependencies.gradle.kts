mapOf(
    // Language
    "kotlinVersion" to "1.3.0",

    // Plugin
    "androidPluginVersion" to "3.3.0-beta04",
    "ktlintGradleVersion" to "6.2.0",
    "dokkaVersion" to "0.9.17",

    // Android
    "minSdkVersion" to 19,
    "compileSdkVersion" to 28,
    "targetSdkVersion" to 28,

    // Library
    "androidxVersion" to "1.0.0",
    "junitVersion" to "4.12",
    "robolectricVersion" to "3.8",
    "mockitoVersion" to "2.22.0",
    "mockitoKotlinVersion" to "2.0.0-RC2"
).forEach { project.extra.set(it.key, it.value) }
