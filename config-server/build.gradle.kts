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

group = "by.vk"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_11

application {
	mainClassName = "by.vk.configserver.ConfigServerApplicationKt"
	applicationName = "config-server"
}

repositories {
	mavenCentral()
}

extra["springCloudVersion"] = "Hoxton.SR6"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web") {
		exclude("org.springframework.boot", "spring-boot-starter-tomcat")
	}
	implementation("org.springframework.boot:spring-boot-starter-jetty")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.springframework.cloud:spring-cloud-config-server")
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
	const val JMX_PORT = "38088"
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