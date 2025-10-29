plugins {
    java
    id("org.springframework.boot") version "3.3.2"
    id("io.spring.dependency-management") version "1.1.5"
}

group = "be.kdg.prog3.hotels"
version = "1.0-SNAPSHOT"
description = "Hotels Management System"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.webjars:bootstrap:5.3.3")
    implementation("org.webjars:webjars-locator-core:0.59")
    implementation("org.webjars.npm:bootstrap-icons:1.11.3")
}

tasks.withType<Test> {
    useJUnitPlatform()
}