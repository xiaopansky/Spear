plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
}

kotlin {
    applyMyHierarchyTemplate()

    androidTarget {
        publishLibraryVariants("release")
        compilations.configureEach {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    jvm("desktop") {
        compilations.configureEach {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        named("commonMain") {
            dependencies {
                api(project(":sketch-core"))
            }
        }
        named("desktopMain") {
            resources.srcDirs("src/images")
        }
        named("iosMain") {
            resources.srcDirs("src/images") // TODO check if this is correct
        }
    }
}

android {
    namespace = "com.github.panpf.sketch.resources"
    compileSdk = property("compileSdk").toString().toInt()

    defaultConfig {
        minSdk = property("minSdk").toString().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
    }

    sourceSets["main"].assets.srcDirs("src/images")

    @Suppress("UnstableApiUsage")
    testOptions {
        targetSdk = property("targetSdk").toString().toInt()
    }

    // Set both the Java and Kotlin compilers to target Java 8.
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}