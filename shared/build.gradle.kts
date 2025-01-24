import com.codingfeline.buildkonfig.compiler.FieldSpec
import org.jetbrains.compose.internal.utils.localPropertiesFile
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.serialization)
    alias(libs.plugins.buildConfig)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    
    jvm()
    
    sourceSets {
        commonMain.dependencies {
            implementation(libs.serialization)
        }
    }
}

android {
    namespace = "com.evandhardspace.yacca.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}

buildkonfig {
    packageName = "com.evandhardspace.yacca"

    defaultConfigs {
        val properties = loadLocalProperties()

        val isLocalImplementationValue = properties["server.mockLocally"].toString().toBool()
        buildConfigField(FieldSpec.Type.BOOLEAN, "isLocalImplementation", isLocalImplementationValue.toString())

        val isEmulatorLocalhost = properties["emulator.localhost"].toString().toBool()
        buildConfigField(FieldSpec.Type.BOOLEAN, "isEmulatorLocalhost", isEmulatorLocalhost.toString())

        val serverLocalPort = properties["server.localPort"]?.toString()?.toIntOrNull()
            ?: if(isEmulatorLocalhost)
                error("emulator.localhost is true but local property server.localPort is not set")
                else 8080
        buildConfigField(FieldSpec.Type.INT, "serverLocalPort", serverLocalPort.toString())

        val baseUrl = properties["server.host"]?.toString().takeUnless { it.isNullOrEmpty() }
            ?: if(isEmulatorLocalhost) "" else error("emulator.localhost is false but local property server.host is not set")
        buildConfigField(FieldSpec.Type.STRING, "baseUrl", baseUrl)
    }
}

private fun String.toBool(): Boolean = toBooleanStrictOrNull() == true

private fun loadLocalProperties(): Properties {
    val properties = Properties()
    val localPropertiesFile = rootProject.localPropertiesFile
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { properties.load(it) }
    }
    return properties
}
