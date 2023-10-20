dependencies{
    implementation(project(":security"))
    //Oauth Clients
    implementation("org.springframework.security:spring-security-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-web")

    testImplementation(project(":security"))
}