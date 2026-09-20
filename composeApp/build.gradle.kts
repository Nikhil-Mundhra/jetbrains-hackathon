plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.application)
}

kotlin {
    androidTarget()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    jvm("desktop")
    wasmJs("wasm")

    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val isMacX64 = hostOs.startsWith("Mac OS X") && System.getProperty("os.arch") == "x86_64"
    val isMacArm64 = hostOs.startsWith("Mac OS X") && System.getProperty("os.arch") == "aarch64"
    val isLinuxX64 = hostOs.startsWith("Linux") && System.getProperty("os.arch") == "x86_64"
    val isLinuxArm64 = hostOs.startsWith("Linux") && System.getProperty("os.arch") == "aarch64"

    val iosTargets = listOf(iosX64(), iosArm64(), iosSimulatorArm64())
    val desktopTargets = listOf(jvm("desktop"))
    val wasmTargets = listOf(wasmJs("wasm"))


    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":shared"))
                implementation(libs.androidx.lifecycle.viewmodel.compose)
                implementation(libs.androidx.navigation.compose)
                implementation(libs.decompose)
                implementation(libs.moko.resources.compose)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.lifecycle.viewmodel.compose)
            }
        }
        val desktopMain by getting {
            dependencies {
                implementation(libs.decompose)
            }
        }
        val wasmJsMain by getting {
            dependencies {
                implementation(libs.compose.web.core)
                implementation(libs.compose.web.dom)
            }
        }
    }
}

android {
    namespace = "com.communityconnect.app"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.yallapark.app"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.6.10"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1,LICENSE,NOTICE}"
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
    }
}

compose {
    plugins {
        id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0"
    }
}