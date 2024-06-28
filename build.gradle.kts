import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.0.0"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.8.20"
    kotlin("plugin.spring") version "1.8.20"
}

buildscript {
    repositories {
        maven("https://plugins.gradle.org/m2/")
        maven {
            url = uri("https://repo.spring.io/snapshot")
        }
        maven {
            url = uri("https://repo.spring.io/milestone")
        }
    }
    dependencies {
        classpath("org.jlleitschuh.gradle:ktlint-gradle:12.1.0")
    }
}

group = "isel.pt.ps"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

allprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.casbin:jcasbin:1.21.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.postgresql:postgresql:42.7.3")
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version = "1.5.+")
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-datetime", version = "0.4.+")
    // To get password encode
    implementation("org.springframework.security:spring-security-web")
    implementation("org.springframework.security:spring-security-config")
    implementation("javax.servlet:javax.servlet-api:4.0.1")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
