plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.nexus.staging)
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

nexusStaging {
    packageGroup = ModuleConfig.groupId
}
