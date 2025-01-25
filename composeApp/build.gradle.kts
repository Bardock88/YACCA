import com.codingfeline.buildkonfig.compiler.FieldSpec
import org.jetbrains.compose.internal.utils.localPropertiesFile
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.serialization)
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
    alias(libs.plugins.buildConfig)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
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
    
    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.ktor.client.okhttp)

            implementation(libs.koin.android)
            implementation(libs.encryptedSharedPreferences)
        }
        commonMain.dependencies {
            implementation(projects.shared)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.materialIconsExtended)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)

            implementation(libs.serialization)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kmpLogging)

            implementation(libs.bundles.mobile.ktorClient)
            implementation(libs.bundles.mobile.koin)
            implementation(libs.bundles.mobile.persistence)

            // fixes transitive dependency
            implementation(libs.stately.common)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.koin.core)
        }
    }
}

android {
    namespace = "com.evandhardspace.yacca"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.evandhardspace.yacca"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug") // todo replace with release signing
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    debugImplementation(compose.uiTooling)
    ksp(libs.room.compiler)
}

buildkonfig {
    packageName = "com.evandhardspace.yacca"

    defaultConfigs {
        val tasks = gradle.startParameter.taskNames
        if("stage" in tasks || tasks.isEmpty())  return@defaultConfigs

        val properties = loadLocalProperties()

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

// todo extract
private fun String.toBool(): Boolean = toBooleanStrictOrNull() == true

private fun loadLocalProperties(): Properties {
    val properties = Properties()
    val localPropertiesFile = rootProject.localPropertiesFile
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { properties.load(it) }
    }
    return properties
}
