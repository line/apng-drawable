rootProject.name = "apng-drawable-root"

pluginManagement {
    repositories {
        gradlePluginPortal()
        jcenter()
        google()
    }
    resolutionStrategy {
        eachPlugin {
            val moduleName = when (requested.id.id) {
                "com.android.application",
                "com.android.library" -> "com.android.tools.build:gradle:${requested.version}"
                "kotlin-android" -> "org.jetbrains.kotlin:kotlin-gradle-plugin:${requested.version}"
                else -> null
            }
            moduleName?.let(::useModule)
        }
    }
}

include(":apng-drawable")
include(":sample-app")

project(":apng-drawable").name = "apng-drawable"
project(":sample-app").name = "sample-app"
