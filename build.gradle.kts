plugins {
    id("com.android.application") version Versions.androidPluginVersion apply false
    id("com.android.library") version Versions.androidPluginVersion apply false
    id("kotlin-android") version Versions.kotlinVersion apply false
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
