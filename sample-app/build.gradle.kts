import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
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
    packagingOptions {
        jniLibs {
            useLegacyPackaging = true
        }
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk7", KotlinCompilerVersion.VERSION))
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.lich.lifecycle)

    implementation(project(":apng-drawable"))
}

