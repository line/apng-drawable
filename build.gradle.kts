buildscript {
    apply(from = "dependencies.gradle.kts")
    val androidPluginVersion: String by rootProject.extra
    val kotlinVersion: String by rootProject.extra
    val dokkaVersion: String by rootProject.extra
    val ktlintGradleVersion: String by rootProject.extra
    repositories {
        google()
        jcenter()
        maven("https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath("com.android.tools.build", "gradle", androidPluginVersion)
        classpath(kotlin("gradle-plugin", version = kotlinVersion))
        classpath("org.jetbrains.dokka", "dokka-android-gradle-plugin", dokkaVersion)
        classpath("gradle.plugin.org.jlleitschuh.gradle", "ktlint-gradle", ktlintGradleVersion)
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

tasks.create("clean", Delete::class.java) {
    delete(rootProject.buildDir)
}
