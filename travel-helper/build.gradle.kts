plugins {
    kotlin("jvm") version "2.3.0"
    kotlin("plugin.spring") version "2.3.0"
    kotlin("plugin.serialization") version "2.3.0"
    id("org.springframework.boot") version "4.0.4"
    id("io.spring.dependency-management") version "1.1.7"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
    maven("https://packages.jetbrains.team/maven/p/grazi/grazie-platform-public")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("ai.koog:koog-spring-boot-starter:1.3.0-beta")
    implementation("ai.koog:koog-agents:1.3.0")
    implementation("ai.koog:a2a-client:1.3.0-beta")
    implementation("ai.koog:a2a-transport-client-jsonrpc-http:1.3.0-beta")
    implementation("io.ktor:ktor-client-cio:3.3.3")
}
