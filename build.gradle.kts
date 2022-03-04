import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// kotlin ver - 1.6.10
val springBootVer: String by project // 2.6.2
val kotlinVer: String by project // 2.6.2
val springSecurityVer: String by project // 5.6.1
val jacksonVer: String by project // 2.13.1
val jwtVer: String by project // 0.9.1
val jsonVer: String by project // 20211205
val jaxbVer: String by project // 2.3.1
val embedMongoVer: String by project // 3.2.4
val mockitoVer: String by project // 2.21.0
val javaJwtVer: String by project // 3.18.2

plugins {
    id("org.springframework.boot") version "2.6.2"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.spring") version "1.6.10"
}

group = "ru.cobalt42"
version = "1.0.0"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb:$springBootVer")
    implementation("org.springframework.boot:spring-boot-starter-data-rest:$springBootVer")
    implementation("org.springframework.boot:spring-boot-starter-security:$springBootVer")
    implementation("org.springframework.boot:spring-boot-starter-web:$springBootVer")
    implementation("org.springframework.boot:spring-boot-starter-actuator:$springBootVer")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVer")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVer")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVer")
    implementation("io.jsonwebtoken:jjwt:$jwtVer")
    implementation("org.json:json:$jsonVer")
    implementation("javax.xml.bind:jaxb-api:$jaxbVer")
    implementation("org.mockito:mockito-core:$mockitoVer")
    implementation("com.auth0:java-jwt:$javaJwtVer")
//	implementation("org.apache.httpcomponents:httpclient:4.5.13")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVer")
    developmentOnly("org.springframework.boot:spring-boot-devtools:$springBootVer")
    testImplementation("org.springframework.boot:spring-boot-starter-test:$springBootVer")
    testImplementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo:$embedMongoVer")
    testImplementation("org.springframework.security:spring-security-test:$springSecurityVer")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
