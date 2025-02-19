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
    implementation(libs.cdimascio.dotenv)
    implementation(libs.postgres)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.zaxxer.hikaricp)
    implementation(libs.h2database)
    testImplementation(libs.ktor.client.core)
    testImplementation(libs.ktor.server.tests)
    testImplementation(libs.ktor.client.contentnegotiation)
    testImplementation(libs.ktor.server.core)
    testImplementation(libs.kotlin.test.junit)
}