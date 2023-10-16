tasks.getByName("bootJar"){
    enabled = false
}

tasks.getByName("jar"){
    enabled = false
}

plugins{
    kotlin("plugin.jpa") version "1.9.10"
}

dependencies{
    //JPA
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    //Dev Database - Mysql
    runtimeOnly("com.h2database:h2")
    runtimeOnly("mysql:mysql-connector-java:8.0.30")

    //Database for OracleDatabase 19c
    runtimeOnly("com.oracle.database.jdbc:ojdbc8")
    implementation("com.oracle.database.jdbc:ucp")
    implementation("com.oracle.database.security:oraclepki")
    implementation ("com.oracle.database.security:osdt_core")
    implementation ("com.oracle.database.security:osdt_cert")
}