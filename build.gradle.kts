group = "by.vk"
version = "0.0.1"

plugins {
    id("org.springframework.boot") version "2.3.1.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    kotlin("jvm") version "1.3.72"
    kotlin("plugin.spring") version "1.3.72"
    id("com.google.cloud.tools.jib") version "2.4.0"
    kotlin("plugin.jpa") version "1.3.72"
    id("org.flywaydb.flyway") version "6.5.0"
}