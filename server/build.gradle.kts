val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val slf4jVersion: String by project

plugins {
    kotlin("jvm")
    id("io.ktor.plugin")
    kotlin("plugin.serialization") version "1.4.21"
}

group = "com.example"
version = "0.0.1"

application {
    mainClass.set("com.example.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("org.jetbrains.exposed", "exposed-core", "0.38.1")
    implementation("org.jetbrains.exposed", "exposed-jdbc", "0.38.1")
    implementation("org.jetbrains.exposed", "exposed-dao", "0.38.1")
    implementation("org.xerial:sqlite-jdbc:3.34.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")

    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
        vendor = JvmVendorSpec.AZUL
    }
}