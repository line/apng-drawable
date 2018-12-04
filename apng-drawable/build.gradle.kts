import com.jfrog.bintray.gradle.BintrayExtension
import org.apache.maven.artifact.ant.InstallTask
import org.gradle.api.internal.plugins.DslObject
import org.jetbrains.dokka.gradle.DokkaAndroidTask
import org.jetbrains.kotlin.config.KotlinCompilerVersion
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("org.jlleitschuh.gradle.ktlint")
    id("org.jetbrains.dokka-android")
    id("com.jfrog.bintray")
    id("com.github.dcendents.android-maven")
}

group = Consts.groupId
version = Consts.artifactId

android {
    defaultConfig {
        minSdkVersion(Versions.minSdkVersion)
        compileSdkVersion(Versions.compileSdkVersion)
        targetSdkVersion(Versions.targetSdkVersion)
        versionName = Consts.version
        version = Consts.version
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

configure<KtlintExtension> {
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
    implementation(kotlin("stdlib-jdk7", KotlinCompilerVersion.VERSION))
    implementation(Libs.androidxAnnotation)

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
        repo = Consts.bintrayRepo
        name = Consts.bintrayName
        userOrg = Consts.bintrayUserOrg
        setLicenses("Apache-2.0")
        websiteUrl = Consts.siteUrl
        issueTrackerUrl = Consts.issueTrackerUrl
        vcsUrl = Consts.vcsUrl
        publicDownloadNumbers = true
        version = VersionConfig().apply {
            name = Consts.version
        }
    })
}

val sourcesJarTask = tasks.create<Jar>("sourcesJar") {
    classifier = "sources"
    from(android.sourceSets["main"].java.srcDirs)
}

tasks.getByName("install", Upload::class).apply {
    DslObject(repositories).convention
        .getPlugin<MavenRepositoryHandlerConvention>()
        .mavenInstaller {
            pom {
                project {
                    packaging = "aar"
                    groupId = Consts.groupId
                    artifactId = Consts.artifactId
                    version = Consts.version
                }
            }
        }
    tasks["bintrayUpload"].dependsOn(this)
}

artifacts {
    add("archives", sourcesJarTask)
}
