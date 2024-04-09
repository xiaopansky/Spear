import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.compose")
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.kotlin.plugin.parcelize")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlinx-atomicfu")
}

kotlin {
    applyMyHierarchyTemplate()

    androidTarget()

    jvm("desktop")

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    js {
        moduleName = "composeApp"
        browser {
            commonWebpackConfig {
                outputFileName = "composeApp.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        androidMain {
            dependencies {
                implementation(project(":sketch-extensions"))
                implementation(project(":sketch-animated-koralgif"))
                implementation(project(":sketch-video"))
                implementation(project(":sketch-video-ffmpeg"))

                implementation(libs.kotlinx.serialization.json)

                implementation(libs.androidx.activity.compose)
//                implementation(libs.androidx.compose.animation)
//                implementation(libs.androidx.compose.foundation)
                implementation(libs.androidx.lifecycle.viewmodel.compose)
//                implementation(libs.androidx.paging.compose)
                implementation(libs.androidx.appcompat)
                implementation(libs.androidx.constraintlayout)
                implementation(libs.androidx.core)
                implementation(libs.androidx.lifecycle.viewmodel)
                implementation(libs.androidx.lifecycle.runtime)
                implementation(libs.androidx.multidex)
                implementation(libs.androidx.navigation.fragment)
                implementation(libs.androidx.navigation.ui)
//                implementation(libs.androidx.paging.common)
//                implementation(libs.androidx.paging.runtime)
                implementation(libs.androidx.recyclerview)
                implementation(libs.androidx.swiperefreshlayout)

                implementation(libs.google.material)
                implementation(libs.panpf.assemblyadapter4.pager)
                implementation(libs.panpf.assemblyadapter4.pager2)
                implementation(libs.panpf.assemblyadapter4.recycler)
                implementation(libs.panpf.assemblyadapter4.recycler.paging)
                implementation(libs.panpf.tools4a.activity)
                implementation(libs.panpf.tools4a.device)
                implementation(libs.panpf.tools4a.display)
                implementation(libs.panpf.tools4a.dimen)
                implementation(libs.panpf.tools4a.fileprovider)
                implementation(libs.panpf.tools4a.network)
                implementation(libs.panpf.tools4a.toast)
                implementation(libs.panpf.tools4j.date)
                implementation(libs.panpf.tools4j.math)
                implementation(libs.panpf.tools4j.io)
                implementation(libs.panpf.tools4j.security)
                implementation(libs.panpf.tools4k)
                implementation(libs.panpf.activitymonitor)
                implementation(libs.panpf.zoomimage.view)
                implementation(libs.tinypinyin)
                implementation(libs.okhttp3.logging)
                implementation(compose.preview)
            }
        }
        androidInstrumentedTest {
            dependencies {
                implementation(project(":internal:test-utils"))
            }
        }
        commonMain {
            dependencies {
                implementation(project(":sketch-compose"))
                implementation(project(":sketch-svg"))
                implementation(project(":sketch-animated"))
                implementation(project(":internal:images"))
                implementation(project(":sketch-extensions-compose"))
                implementation(compose.material)
                implementation(compose.material3)
                implementation(compose.runtime)
                implementation(compose.ui)
                implementation(compose.uiUtil)
                implementation(compose.components.resources)
                implementation(libs.ktor.client.contentNegotiation)
                implementation(libs.ktor.serialization.kotlinxJson)
                implementation(libs.voyager.navigator)
                implementation(libs.voyager.screenModel)
                implementation(libs.voyager.bottomSheetNavigator)
                implementation(libs.voyager.tabNavigator)
                implementation(libs.voyager.transitions)
            }
        }
        desktopMain {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.harawata.appdirs)
                implementation(libs.panpf.zoomimage.compose)
                implementation(compose.preview)
            }
        }

        jvmCommonMain {
            dependencies {
                implementation(project(":sketch-okhttp"))
                implementation(libs.panpf.zoomimage.compose)
            }
        }

        iosMain {
            // It will not be transferred automatically and needs to be actively configured.. This may be a bug of kmp.
            resources.srcDirs("../internal/images/files")
        }

        nonJsCommonMain {
            dependencies {
                implementation(libs.cashapp.paging.compose.common)
                implementation(libs.androidx.datastore.core.okio)
                implementation(libs.androidx.datastore.preferences.core)
            }
        }

        jsCommonMain {

        }
    }
}

compose.desktop {
    application {
        mainClass = "com.github.panpf.sketch.sample.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.github.panpf.sketch4.sample"
            packageVersion = property("versionName").toString().let {
                if (it.contains("-")) {
                    it.substring(0, it.indexOf("-"))
                } else {
                    it
                }
            }
        }
    }
}

androidApplication(
    nameSpace = "com.github.panpf.sketch.sample",
    applicationId = "com.github.panpf.sketch3.sample"
) {
    signingConfigs {
        create("sample") {
            storeFile = project.file("sample.keystore")
            storePassword = "B027HHiiqKOMYesQ"
            keyAlias = "panpf-sample"
            keyPassword = "B027HHiiqKOMYesQ"
        }
    }
    buildTypes {
        debug {
            multiDexEnabled = true
            signingConfig = signingConfigs.getByName("sample")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("sample")
        }
    }

    flavorDimensions.add("default")

    androidResources {
        noCompress.add("bmp")
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
//        compose = true
    }

//    composeOptions {
//        kotlinCompilerExtensionVersion = libs.versions.jetbrains.compose.compiler.get()
//    }

    applicationVariants.all {
        val variant = this
        variant.outputs.all {
            val output = this
            if (output is com.android.build.gradle.internal.api.BaseVariantOutputImpl) {
                output.outputFileName =
                    "sketch-sample-${variant.name}-${variant.versionName}.apk"
            }
        }
    }

    dependencies {
        debugImplementation(libs.leakcanary)
    }
}

// https://youtrack.jetbrains.com/issue/KT-56025
afterEvaluate {
    tasks {
        val configureJs: Task.() -> Unit = {
            dependsOn(named("jsDevelopmentExecutableCompileSync"))
            dependsOn(named("jsProductionExecutableCompileSync"))
            dependsOn(named("jsTestTestDevelopmentExecutableCompileSync"))

//            dependsOn(named("wasmJsDevelopmentExecutableCompileSync"))
//            dependsOn(named("wasmJsProductionExecutableCompileSync"))
//            dependsOn(named("wasmJsTestTestDevelopmentExecutableCompileSync"))
        }
        named("jsBrowserProductionWebpack").configure(configureJs)
//        named("wasmJsBrowserProductionExecutableDistributeResources").configure(configureJs)
    }
}

// https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-images-resources.html
// The current 1.6.1 version only supports the use of compose resources in the commonMain source set of the Feiku module. The files of the images module can only be added to the js module in this way.
tasks.register<Copy>("copyResources") {
    from(project(":internal:images").file("files"))
    into(project(":sample").file("build/processedResources/js/main/files"))
}
tasks.named("jsProcessResources") {
    dependsOn("copyResources")
}