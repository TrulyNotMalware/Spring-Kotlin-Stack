tasks.getByName("bootJar"){
    enabled = false
}

tasks.getByName("jar"){
    enabled = false
}

dependencies {
    api(project(":core"))
    api(project(":common"))
    implementation("org.springframework.boot:spring-boot-starter-web")

    //Random UUID Generator
    api("com.fasterxml.uuid:java-uuid-generator:4.3.0")

    //HATEOAS swagger
    implementation("org.springdoc:springdoc-openapi-hateoas:1.7.0"){
    // Common 1.7 모듈이 ui 2.X 와 호환되지 않음.
        exclude (group ="org.springdoc", module = "springdoc-openapi-common")
    }
    implementation("org.springdoc:springdoc-openapi-starter-common:2.1.0")// 별도로 추가.
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0")

    api("org.springframework.boot:spring-boot-starter-security")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}