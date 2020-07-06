import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    id("org.springframework.boot") version "2.3.1.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    kotlin("jvm") version "1.3.72"
    kotlin("plugin.spring") version "1.3.72"
    kotlin("plugin.jpa") version "1.3.72"
    id("com.google.cloud.tools.jib") version "2.4.0"
}

springBoot {
    buildInfo()
}

group = "by.vk"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_11

application {
    mainClassName = "com.kafka.producer.demo.NotificationConsumerKt"
    applicationName = "kafka-consumer"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude("org.springframework.boot", "spring-boot-starter-tomcat")
    }
    implementation("org.springframework.boot:spring-boot-starter-jetty")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("com.h2database:h2") //todo remove after test containter injection
    implementation("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("org.springframework.kafka:spring-kafka-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

kotlin {
    sourceSets["test"].apply {
        kotlin.srcDirs("src/test/kotlin/unit", "src/test/kotlin/integration")
    }
}


object DockerProps {
    const val BASE_IMAGE = "gcr.io/distroless/java:11"
    const val APP_PORT = "8081"
    const val JMX_PORT = "38081"
}

jib {
    from {
        image = DockerProps.BASE_IMAGE
    }
    to {
        image = "registry.hub.docker.com/${System.getenv("DOCKER_HUB_USER")}/${application.applicationName}:${version}"
        auth {
            username = System.getenv("DOCKER_HUB_USER")
            password = System.getenv("DOCKER_HUB_PASSWORD")
        }
        tags = setOf("$version", "latest")
    }
    container {
        jvmFlags =
            org.jetbrains.kotlin.util.parseSpaceSeparatedArgs("-noverify -Dspring.profiles.active=dev -Djava.rmi.server.hostname=localhost -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=${DockerProps.JMX_PORT} -Dcom.sun.management.jmxremote.rmi.port=${DockerProps.JMX_PORT}")
        ports = listOf(DockerProps.APP_PORT, DockerProps.JMX_PORT)
        labels = mapOf("app-name" to application.applicationName, "service-version" to version.toString())
    }
}