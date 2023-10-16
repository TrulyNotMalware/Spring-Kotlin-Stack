tasks.getByName("bootJar"){
    enabled = false
}

tasks.getByName("jar"){
    enabled = false
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    //Jwt Token
    api("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
}
