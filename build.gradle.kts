buildscript {
    repositories {
        google()
        jcenter()
        maven("https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath(Libs.androidGradlePlugin)
        classpath(kotlin("gradle-plugin", version = Versions.kotlinVersion))
        classpath(Libs.dokkaAndroidGradlePlugin)
        classpath(Libs.ktlintGradlePlugin)
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
