plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.serialization)
    application
}

group = "com.evandhardspace.yacca"
version = "1.0.0"
application {
    mainClass.set("com.evandhardspace.yacca.ApplicationKt")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=${extra["io.ktor.development"] ?: "false"}")
}

dependencies {
    implementation(projects.shared)
    implementation(libs.logback)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.authJwt)
    implementation(libs.ktor.server.logging)
    implementation(libs.ktor.server.serialization)
    implementation(libs.ktor.server.contentNegotiation)
    implementation(libs.commons.codec)

    testImplementation(libs.kotlin.test.junit)
}