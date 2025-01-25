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
    implementation(libs.commons.codec)

    implementation(libs.bundles.server.ktorServer)
    implementation(libs.bundles.server.database)
    implementation(libs.bundles.server.ktorClient)

    testImplementation(libs.kotlin.test.junit)
}