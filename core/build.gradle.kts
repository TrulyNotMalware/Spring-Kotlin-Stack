import org.gradle.api.tasks.bundling.Jar
import org.springframework.boot.gradle.tasks.bundling.BootJar

val jar: Jar by tasks
val bootJar: BootJar by tasks

jar.enabled = true
bootJar.enabled = false

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
    implementation ("com.oracle.database.security:osdt_core:21.11.0.0")
    implementation ("com.oracle.database.security:osdt_cert:21.11.0.0")
    //Include Hateoas
    api("org.springframework.hateoas:spring-hateoas")

    //Spring boot test.
    testFixturesApi("org.springframework.boot:spring-boot-starter-test")
}