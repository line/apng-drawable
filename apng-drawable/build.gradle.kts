import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ktlint.gradle)
    alias(libs.plugins.dokka)
    alias(libs.plugins.gradle.versions.plugin)
    `maven-publish`
    signing
}

group = "com.linecorp"
version = libs.versions.apng.drawable.get()

android {
    defaultConfig {
        namespace = "com.linecorp.apng"
        minSdk = libs.versions.build.minSdk.get().toInt()
        compileSdk = libs.versions.build.compileSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles(
            file("proguard-rules.pro")
        )
        externalNativeBuild {
            cmake {
                cppFlags += "-std=c++17"
                cppFlags += "-fno-rtti"
                cppFlags += "-fexceptions"
                arguments += listOf("-DANDROID_SUPPORT_FLEXIBLE_PAGE_SIZES=ON")
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
    ndkVersion = libs.versions.build.ndk.get()
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
    buildFeatures {
        buildConfig = true
    }
    lint {
        xmlReport = true
    }
}

ktlint {
    android.set(true)
    reporters {
        reporter(ReporterType.CHECKSTYLE)
    }
}

dependencies {
    api(libs.androidx.annotation)
    api(libs.androidx.animated.vectordrawable)

    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.mockito)
    testImplementation(libs.mockito.kotlin)
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
                groupId = "com.linecorp"
                artifactId = "apng"
                version = libs.versions.apng.drawable.get()
                pom {
                    packaging = "aar"
                    name.set("ApngDrawable")
                    description.set("Fast and light weight Animated Portable Network Graphics(APNG) image decoder library for Android platform")
                    url.set("https://github.com/line/apng-drawable")
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
                        connection.set("scm:git:git://github.com/line/apng-drawable.git")
                        developerConnection.set("scm:git:ssh://github.com:line/apng-drawable.git")
                        url.set("https://github.com/line/apng-drawable/tree/master")
                    }
                    issueManagement {
                        system.set("GitHub")
                        url.set("https://github.com/line/apng-drawable/issues")
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
                val repositoryUrl = if (libs.versions.apng.drawable.get().endsWith("SNAPSHOT")) {
                    snapshotRepositoryUrl
                } else {
                    releaseRepositoryUrl
                }
                val repositoryUsername: String? by project
                val repositoryPassword: String? by project

                setUrl(repositoryUrl)
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
