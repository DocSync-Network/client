import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    kotlin("plugin.serialization") version "2.0.20"
}

kotlin {
    jvm("desktop")
    
    sourceSets {
        val desktopMain by getting
        
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)

            // Ktor
            implementation("io.ktor:ktor-client-core:3.0.0-rc-1")
            implementation("io.ktor:ktor-client-serialization:3.0.0-rc-1")
            implementation("io.ktor:ktor-client-websockets:3.0.0-rc-1")
            implementation("io.ktor:ktor-client-content-negotiation:3.0.0-rc-1")
            implementation("io.ktor:ktor-client-logging:3.0.0-rc-1")
            implementation("io.ktor:ktor-client-cio:3.0.0-rc-1")

            // Serialization
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

            // Key value data storage
            implementation("com.russhwolf:multiplatform-settings-no-arg:1.2.0")

            // ViewModel
            implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")

            // icons
            implementation(compose.materialIconsExtended)

            // koin
            implementation("io.insert-koin:koin-core:4.0.0")
            implementation("io.insert-koin:koin-compose-viewmodel:4.0.0")
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}

compose.desktop {
    application {
        mainClass = "org.dvir.docsync.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Exe)
            packageName = "org.dvir.docsync"
            packageVersion = "1.0.0"
        }
    }
}
