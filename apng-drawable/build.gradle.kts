import com.jfrog.bintray.gradle.BintrayExtension
import org.gradle.api.internal.plugins.DslObject
import org.jetbrains.dokka.gradle.DokkaAndroidTask
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("org.jlleitschuh.gradle.ktlint") version Versions.ktlintGradleVersion
    id("org.jetbrains.dokka-android") version Versions.dokkaVersion
    id("com.jfrog.bintray") version Versions.bintrayGradlePluginVersion
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
                    getcFlags() += "-DBUILD_DEBUG"
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

ktlint {
    android.set(true)
    reporters.set(setOf(ReporterType.CHECKSTYLE))
    ignoreFailures.set(true)
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
    api(kotlin("stdlib-jdk7", Versions.kotlinVersion))
    api(Libs.androidxAnnotation)
    api(Libs.androidxVectorDrawable)

    testImplementation(Libs.junit)
    testImplementation(Libs.robolectric)
    testImplementation(Libs.mockitoInline)
    testImplementation(Libs.mockitoKotlin)
}

bintray {
    user = project.properties["bintray.user"]?.toString() ?: ""
    key = project.properties["bintray.api_key"]?.toString() ?: ""
    setConfigurations("archives")

    pkg(delegateClosureOf<BintrayExtension.PackageConfig> {
        repo = ModuleConfig.bintrayRepo
        name = ModuleConfig.bintrayName
        userOrg = ModuleConfig.bintrayUserOrg
        setLicenses("Apache-2.0")
        websiteUrl = ModuleConfig.siteUrl
        issueTrackerUrl = ModuleConfig.issueTrackerUrl
        vcsUrl = ModuleConfig.vcsUrl
        publicDownloadNumbers = true
        version = VersionConfig().apply {
            name = ModuleConfig.version
        }
    })
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
    tasks["bintrayUpload"].dependsOn(this)
}

artifacts {
    add("archives", sourcesJarTask)
}
