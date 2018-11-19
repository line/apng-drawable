import org.jetbrains.dokka.gradle.DokkaAndroidTask
import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("org.jlleitschuh.gradle.ktlint")
    id("org.jetbrains.dokka-android")
}

android {
    defaultConfig {
        val minSdkVersion: Int by rootProject.extra
        val compileSdkVersion: Int by rootProject.extra
        val targetSdkVersion: Int by rootProject.extra
        minSdkVersion(minSdkVersion)
        compileSdkVersion(compileSdkVersion)
        targetSdkVersion(targetSdkVersion)
        versionName = project.properties["apng_drawable.version"]!!.toString()
        version = project.properties["apng_drawable.version"]!!
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles(
            file("proguard-rules.pro")
        )
        externalNativeBuild {
            cmake {
                cppFlags += "-std=c++17"
                cppFlags += "-fno-rtti"
                cppFlags += "-fno-exceptions"
            }
        }
    }
    sourceSets {
        getByName("main").java.srcDirs("src/main/kotlin")
        getByName("test").java.srcDirs("src/test/kotlin")
        getByName("androidTest").java.srcDirs("src/androidTest/kotlin")
    }
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            isUseProguard = false
            externalNativeBuild {
                cmake {
                    arguments += "-DCMAKE_BUILD_TYPE=DEBUG"
                    cppFlags += "-DBUILD_DEBUG"
                    getcFlags() += "-DBUILD_DEBUG"
                }
            }
        }
        getByName("release") {
            isMinifyEnabled = true
            isUseProguard = true
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                file("proguard-rules.pro")
            )
            externalNativeBuild {
                cmake {
                    arguments += "-DCMAKE_BUILD_TYPE=RELEASE"
                    cppFlags -= "-DBUILD_DEBUG"
                    getcFlags() -= "-DBUILD_DEBUG"
                }
            }
        }
    }
    externalNativeBuild {
        cmake {
            setPath("src/main/cpp/CMakeLists.txt")
        }
    }
}

tasks.withType(DokkaAndroidTask::class.java) {
    // https://github.com/Kotlin/dokka/issues/229
    reportUndocumented = false
    outputDirectory = "$buildDir/javadoc"
    outputFormat = "javadoc"
    includeNonPublic = true
    includes = listOf("doc/module_package.md")
    jdkVersion = 7
}


dependencies {
    val androidxVersion: String by rootProject.extra
    val junitVersion: String by rootProject.extra
    val robolectricVersion: String by rootProject.extra
    val mockitoVersion: String by rootProject.extra
    val mockitoKotlinVersion: String by rootProject.extra

    implementation(kotlin("stdlib-jdk7", KotlinCompilerVersion.VERSION))
    implementation("androidx.annotation", "annotation", androidxVersion)

    testImplementation("junit", "junit", junitVersion)
    testImplementation("org.robolectric", "robolectric", robolectricVersion)
    testImplementation("org.mockito", "mockito-inline", mockitoVersion)
    testImplementation("com.nhaarman.mockitokotlin2", "mockito-kotlin", mockitoKotlinVersion)
}
