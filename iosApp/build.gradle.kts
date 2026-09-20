plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach { target ->
        target.compilations["main"].defaultSourceSet {
            dependencies {
                implementation(project(":composeApp"))
            }
        }
    }

    targets.withType(org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget::class.java).configureEach {
        binaries {
            framework {
                baseName = "ComposeApp"
                isStatic = false
                transitiveExport = true
            }
        }
    }
}

// Xcode integration
tasks.named("compileKotlinIosX64").configure {
    doLast {
        println("iOS X64 framework built at: ${buildDir}/bin/iosX64/debugFramework/ComposeApp.framework")
    }
}
tasks.named("compileKotlinIosArm64").configure {
    doLast {
        println("iOS ARM64 framework built at: ${buildDir}/bin/iosArm64/debugFramework/ComposeApp.framework")
    }
}
tasks.named("compileKotlinIosSimulatorArm64").configure {
    doLast {
        println("iOS Simulator ARM64 framework built at: ${buildDir}/bin/iosSimulatorArm64/debugFramework/ComposeApp.framework")
    }
}