import org.gradle.api.tasks.bundling.Jar
import org.springframework.boot.gradle.tasks.bundling.BootJar

val jar: Jar by tasks
val bootJar: BootJar by tasks

jar.enabled = true
bootJar.enabled = false

plugins{
    kotlin("plugin.jpa") version "1.9.10"
}

dependencies{
    //JPA
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api("org.springframework.boot:spring-boot-starter-validation")

    //Dev Database - Mysql
    runtimeOnly("com.h2database:h2")
    runtimeOnly("mysql:mysql-connector-java:8.0.30")

    //Database for OracleDatabase 19c
    runtimeOnly("com.oracle.database.jdbc:ojdbc8")
    implementation("com.oracle.database.jdbc:ucp")
    implementation("com.oracle.database.security:oraclepki")
    implementation ("com.oracle.database.security:osdt_core")
    implementation ("com.oracle.database.security:osdt_cert")
    //Include Hateoas
    api("org.springframework.hateoas:spring-hateoas")
}