buildscript {
    repositories {
        google()
        jcenter()
        maven("https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath("com.android.tools.build", "gradle", Versions.androidPluginVersion)
        classpath(kotlin("gradle-plugin", version = Versions.kotlinVersion))
        classpath("org.jetbrains.dokka", "dokka-android-gradle-plugin", Versions.dokkaVersion)
        classpath(
            "org.jlleitschuh.gradle",
            "ktlint-gradle",
            Versions.ktlintGradleVersion
        )
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
