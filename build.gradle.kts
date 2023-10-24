import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.10"
    id("io.spring.dependency-management") version "1.1.3"
    kotlin("plugin.spring") version "1.9.10" apply false
    kotlin("plugin.jpa") version "1.9.10"
    id("org.springframework.boot") version "3.1.1" apply false
}

java.sourceCompatibility = JavaVersion.VERSION_17

allprojects{
    group = "dev.notypie"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }

    tasks.withType<JavaCompile>{
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions{
            freeCompilerArgs += "-Xjsr305=strict"
            jvmTarget = "17"
        }
    }
}

subprojects{

    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    dependencies {
        //Kotlin Reflection
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

        //Test implementation
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("io.projectreactor:reactor-test")
    }
}