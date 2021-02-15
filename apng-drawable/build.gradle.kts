import org.gradle.api.internal.plugins.DslObject
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("org.jlleitschuh.gradle.ktlint") version Versions.ktlintGradleVersion
    id("org.jetbrains.dokka") version Versions.dokkaVersion
    id("com.github.dcendents.android-maven") version Versions.androidMavenGradlePluginVersion
    id("com.github.ben-manes.versions") version Versions.gradleVersionsPluginVersion
}

group = ModuleConfig.groupId
version = ModuleConfig.artifactId

android {
    defaultConfig {
        minSdkVersion(Versions.minSdkVersion)
        compileSdkVersion(Versions.compileSdkVersion)
        targetSdkVersion(Versions.targetSdkVersion)
        versionName = ModuleConfig.version
        version = ModuleConfig.version
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
        debug {
            isMinifyEnabled = false
            isUseProguard = false
            externalNativeBuild {
                cmake {
                    arguments += "-DCMAKE_BUILD_TYPE=DEBUG"
                    cppFlags += "-DBUILD_DEBUG"
                    cFlags += "-DBUILD_DEBUG"
                }
            }
        }
        release {
            isMinifyEnabled = false
            isUseProguard = false
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                file("proguard-rules.pro")
            )
            externalNativeBuild {
                cmake {
                    arguments += "-DCMAKE_BUILD_TYPE=RELEASE"
                    cppFlags -= "-DBUILD_DEBUG"
                    cFlags -= "-DBUILD_DEBUG"
                }
            }
        }
    }
    ndkVersion = Versions.ndkVersion
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }
}

ktlint {
    android.set(true)
    reporters {
        reporter(ReporterType.CHECKSTYLE)
    }
    ignoreFailures.set(true)
}

dependencies {
    api(kotlin("stdlib-jdk7", Versions.kotlinVersion))
    api(Libs.androidxAnnotation)
    api(Libs.androidxVectorDrawable)

    testImplementation(Libs.junit)
    testImplementation(Libs.robolectric)
    testImplementation(Libs.mockitoInline)
    testImplementation(Libs.mockitoKotlin)
}

val sourcesJarTask = tasks.create<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(android.sourceSets["main"].java.srcDirs)
}

tasks.getByName("install", Upload::class).apply {
    DslObject(repositories).convention
        .getPlugin<MavenRepositoryHandlerConvention>()
        .mavenInstaller {
            pom {
                project {
                    packaging = "aar"
                    groupId = ModuleConfig.groupId
                    artifactId = ModuleConfig.artifactId
                    version = ModuleConfig.version
                    withGroovyBuilder {
                        "licenses" {
                            // License for apng-drawable itself
                            "license" {
                                setProperty("name", "Apache-2.0")
                                setProperty("url", "https://www.apache.org/licenses/LICENSE-2.0.txt")
                                setProperty("distribution", "repo")
                            }
                            // License for libpng/apng-patch
                            "license" {
                                setProperty("name", "Zlib")
                                setProperty("url", "http://www.zlib.net/zlib_license.html")
                                setProperty("distribution", "repo")
                            }
                        }
                    }
                }
            }
        }
}

artifacts {
    add("archives", sourcesJarTask)
}
