plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.serialization)
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

    listOf(iosTargets, desktopTargets, wasmTargets).flatten().forEach { target ->
        target.compilations["main"].defaultSourceSet {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.datetime)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.contentNegotiation)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.serialization.kotlinxJson)
                implementation(libs.ktor.client.cio)
                implementation(libs.kermit)
                implementation(libs.coil.compose)
                implementation(libs.room.runtime)
                implementation(libs.room.compiler)
                implementation(libs.sqlDelight.runtime)
                implementation(libs.sqlDelight.coroutines)
                implementation(libs.decompose)
                implementation(libs.mokoResources)
                implementation(libs.mokoResourcesCompose)
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.datetime)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.contentNegotiation)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.serialization.kotlinxJson)
                implementation(libs.ktor.client.cio)
                implementation(libs.kermit)
                implementation(libs.coil.compose)
                implementation(libs.room.runtime)
                implementation(libs.sqlDelight.runtime)
                implementation(libs.sqlDelight.coroutines)
                implementation(libs.decompose)
                implementation(libs.mokoResources)
                implementation(libs.mokoResourcesCompose)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.lifecycle.runtime)
                implementation(libs.androidx.lifecycle.viewmodel)
                implementation(libs.androidx.lifecycle.viewmodelCompose)
                implementation(libs.androidx.navigation.compose)
                implementation(libs.androidx.activityCompose)
                implementation(libs.google.mapsCompose)
                implementation(libs.location)
            }
        }
        val iosMain by getting {
            dependencies {
                implementation(libs.ktor.client.darwin)
            }
        }
        val desktopMain by getting {
            dependencies {
                implementation(libs.ktor.client.jvm)
            }
        }
        val wasmJsMain by getting {
            dependencies {
                implementation(libs.ktor.client.js)
                implementation(libs.compose.web.core)
                implementation(libs.compose.web.dom)
            }
        }
    }
}

android {
    namespace = "com.communityconnect.shared"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
        targetSdk = 34
    }
}

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile).configureEach {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
    }
}

compose {
    plugins {
        id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0"
    }
}