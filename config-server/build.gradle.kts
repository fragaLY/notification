import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.util.parseSpaceSeparatedArgs

plugins {
    application
    id("org.springframework.boot") version "2.5.2"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.5.20"
    kotlin("plugin.spring") version "1.5.20"
    id("com.google.cloud.tools.jib") version "3.1.1"
}

group = "by.vk"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_11

application {
    mainClass.set("by.vk.configserver.ConfigServerApplicationKt")
    applicationName = "config-server"
}

repositories {
    mavenCentral()
}

extra["springCloudVersion"] = "2020.0.3"

configurations.all {
    exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-jetty")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.cloud:spring-cloud-config-server")
    implementation("ch.qos.logback.contrib:logback-json-classic:0.1.5")
    implementation("ch.qos.logback.contrib:logback-jackson:0.1.5")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

object DockerProps {
    const val BASE_IMAGE = "gcr.io/distroless/java:11"
    const val APP_PORT = "8088"
    const val DEBUG_PORT = "5088"
    const val JMX_PORT = "38088"
}

object JVMProps {
    const val XMX = "256m"
    const val XMS = "64m"
    const val MAX_METASPACE_SIZE = "64m"
    const val MAX_DIRECT_MEMORY_SIZE = "128m"
    const val HEAPDUMP_PATH = "/opt/tmp/heapdump.bin"
}

jib {
    from {
        image = DockerProps.BASE_IMAGE
    }
    container {
        val env = HashMap<String, String>()
        env["GIT_USERNAME"] = System.getenv("GIT_USERNAME")
        env["GIT_PASSWORD"] = System.getenv("GIT_PASSWORD")
        env["GIT_CONFIG_URL"] = System.getenv("GIT_CONFIG_URL")
        environment = env
		environment = mapOf(
				"USERNAME" to "super_secret_user",
				"PASSWORD" to "super_secret_password")
        creationTime = "USE_CURRENT_TIMESTAMP"
        jvmFlags = parseSpaceSeparatedArgs("-noverify -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0 -XX:+OptimizeStringConcat -XX:+UseStringDeduplication -XX:+ExitOnOutOfMemoryError -XX:+AlwaysActAsServerClassMachine -Xlog:gc -Xmx${JVMProps.XMX} -Xms${JVMProps.XMS} -XX:MaxMetaspaceSize=${JVMProps.MAX_METASPACE_SIZE} -XX:MaxDirectMemorySize=${JVMProps.MAX_DIRECT_MEMORY_SIZE} -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${JVMProps.HEAPDUMP_PATH} -Djava.rmi.server.hostname=localhost -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=${DockerProps.JMX_PORT} -Dcom.sun.management.jmxremote.rmi.port=${DockerProps.JMX_PORT} -Dspring.profiles.active=prod")
        ports = listOf(DockerProps.APP_PORT, DockerProps.DEBUG_PORT, DockerProps.JMX_PORT)
        labels.set(mapOf("maintainer" to "Vadzim Kavalkou <vadzim.kavalkou@gmail.com>",
                "app-name" to application.applicationName,
                "service-version" to version.toString()))
    }
}
