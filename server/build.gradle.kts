plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    application
}

group = "com.holden"
version = "1.0.0"
application {
    mainClass.set("com.holden.ApplicationKt")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=${extra["io.ktor.development"] ?: "false"}")
}

dependencies {
    implementation(projects.shared)
    implementation(libs.logback)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.contentnegotiation)
    implementation(libs.ktor.server.json)
    testImplementation(libs.ktor.client.core)
    testImplementation(libs.ktor.server.tests)
    testImplementation(libs.ktor.server.core)
    testImplementation(libs.kotlin.test.junit)
}