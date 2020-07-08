import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.util.parseSpaceSeparatedArgs

plugins {
    application
    id("org.springframework.boot") version "2.3.1.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    kotlin("jvm") version "1.3.72"
    kotlin("plugin.spring") version "1.3.72"
    id("com.google.cloud.tools.jib") version "2.4.0"
}

springBoot {
    buildInfo()
}

group = "by.vk"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_11

application {
    mainClassName = "com.api.gateway.demo.DemoApplicationKt"
    applicationName = "kafka-producer"
}

repositories {
    mavenCentral()
}

extra["springCloudVersion"] = "Hoxton.SR6"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.cloud:spring-cloud-starter-gateway")
    implementation("org.springframework.cloud:spring-cloud-config-client")
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-reactor-resilience4j")
    implementation("io.github.resilience4j:resilience4j-spring-boot2:1.5.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

object DockerProps {
    const val BASE_IMAGE = "gcr.io/distroless/java:11"
    const val APP_PORT = "8082"
    const val JMX_PORT = "38082"
}

jib {
    from {
        image = DockerProps.BASE_IMAGE
    }
    container {
        jvmFlags = parseSpaceSeparatedArgs("-noverify -Djava.rmi.server.hostname=localhost -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=${DockerProps.JMX_PORT} -Dcom.sun.management.jmxremote.rmi.port=${DockerProps.JMX_PORT}")
        ports = listOf(DockerProps.APP_PORT, DockerProps.JMX_PORT)
        labels = mapOf("app-name" to application.applicationName, "service-version" to version.toString())
    }
}

