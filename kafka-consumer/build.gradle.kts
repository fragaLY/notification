import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.util.parseSpaceSeparatedArgs

plugins {
    application
    id("org.springframework.boot") version "2.3.1.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    kotlin("jvm") version "1.3.72"
    kotlin("plugin.spring") version "1.3.72"
    kotlin("plugin.jpa") version "1.3.72"
    id("com.google.cloud.tools.jib") version "2.4.0"
    id("org.flywaydb.flyway") version "6.5.0"
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
    implementation("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("org.springframework.kafka:spring-kafka-test")
    testImplementation("org.testcontainers:postgresql:1.14.+")
    testImplementation("org.testcontainers:junit-jupiter:1.14.+")
    testImplementation("com.h2database:h2")
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
    container {
        jvmFlags =
            parseSpaceSeparatedArgs("-noverify -Djava.rmi.server.hostname=localhost -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=${DockerProps.JMX_PORT} -Dcom.sun.management.jmxremote.rmi.port=${DockerProps.JMX_PORT}")
        ports = listOf(DockerProps.APP_PORT, DockerProps.JMX_PORT)
        labels = mapOf("app-name" to application.applicationName, "service-version" to version.toString())
    }
}

// object FlywayProps {
//     const val DRIVER = "org.postgresql.Driver"
//     const val HOST = "localhost"
//     const val PORT = "5432"
//     const val SCHEMA = "notifications"
//     const val DB = "notifications"
//     const val USER = "user"
//     const val PASSWORD = "P@55w0rd"
// }
//
// flyway {
//     driver = FlywayProps.DRIVER
//     url =
//         "jdbc:postgresql://${FlywayProps.HOST}:${FlywayProps.PORT}/${FlywayProps.DB}?currentSchema=${FlywayProps.SCHEMA}"
//     user = FlywayProps.USER
//     password = FlywayProps.PASSWORD
//     schemas = arrayOf(FlywayProps.SCHEMA)
//     locations = arrayOf("src/main/resources/db/migration")
// }