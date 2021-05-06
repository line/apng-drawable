import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    compileSdkVersion(Versions.compileSdkVersion)
    defaultConfig {
        applicationId = "com.linecorp.apngsample"
        minSdkVersion(Versions.minSdkVersion)
        targetSdkVersion(Versions.targetSdkVersion)
        versionCode = 1
        versionName = "1.0"
        missingDimensionStrategy("env", "androidx")
    }
    sourceSets {
        getByName("main").java.srcDirs("src/main/kotlin")
    }
    buildTypes {
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"), file("proguard-rules.pro")
            )
        }
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk7", KotlinCompilerVersion.VERSION))
    implementation(Libs.androidxAppcompat)
    implementation(Libs.androidxConstraintLayout)
    implementation(Libs.kotlinxCoroutines)
    implementation(Libs.lichLifecycle)

    implementation(project(":apng-drawable"))
}

