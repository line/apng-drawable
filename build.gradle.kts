plugins {
    id("com.android.application") version Versions.androidPluginVersion apply false
    id("com.android.library") version Versions.androidPluginVersion apply false
    id("kotlin-android") version Versions.kotlinVersion apply false
    id("io.codearte.nexus-staging") version Versions.nexusStagingVersion
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

nexusStaging {
    packageGroup = ModuleConfig.groupId
}

tasks.create("clean", Delete::class.java) {
    delete(rootProject.buildDir)
}
