import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("org.jlleitschuh.gradle.ktlint") version Versions.ktlintGradleVersion
    id("org.jetbrains.dokka") version Versions.dokkaVersion
    id("com.github.dcendents.android-maven") version Versions.androidMavenGradlePluginVersion
    id("com.github.ben-manes.versions") version Versions.gradleVersionsPluginVersion
    `maven-publish`
    signing
}

group = ModuleConfig.groupId
version = ModuleConfig.version

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

val javadocJarTask = tasks.create<Jar>("javadocJar") {
    archiveClassifier.set("javadoc")
    from(tasks.getByName("dokkaJavadoc"))
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("apngDrawable") {
                groupId = ModuleConfig.groupId
                artifactId = ModuleConfig.artifactId
                version = ModuleConfig.version
                pom {
                    packaging = "aar"
                    name.set(ModuleConfig.name)
                    description.set(ModuleConfig.description)
                    url.set(ModuleConfig.siteUrl)
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                            distribution.set("repo")
                        }
                    }
                    developers {
                        developer {
                            name.set("LINE Corporation")
                            email.set("dl_oss_dev@linecorp.com")
                            url.set("https://engineering.linecorp.com/en/")
                        }
                    }
                    scm {
                        connection.set(ModuleConfig.scmConnectionUrl)
                        developerConnection.set(ModuleConfig.scmDeveloperConnectionUrl)
                        url.set(ModuleConfig.scmUrl)
                    }
                    issueManagement {
                        system.set("GitHub")
                        url.set(ModuleConfig.issueTrackerUrl)
                    }
                }

                from(components["release"])
                artifact(sourcesJarTask)
                artifact(javadocJarTask)
            }
        }
        repositories {
            maven {
                name = "MavenCentral"
                val releaseRepositoryUrl =
                    "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
                val snapshotRepositoryUrl =
                    "https://oss.sonatype.org/content/repositories/snapshots/"
                val repositoryUrl = if (ModuleConfig.version.endsWith("SNAPSHOT")) {
                    snapshotRepositoryUrl
                } else {
                    releaseRepositoryUrl
                }
                val repositoryUsername: String? by project
                val repositoryPassword: String? by project

                url = uri(repositoryUrl)
                credentials {
                    username = repositoryUsername ?: ""
                    password = repositoryPassword ?: ""
                }
            }
        }
    }
    signing {
        val signingKey: String? by project
        val signingPassword: String? by project

        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications["apngDrawable"])
    }
}

artifacts {
    add("archives", sourcesJarTask)
}
