dependencies{

    implementation(project(":security"))

    //Authorization-Server
    implementation("org.springframework.boot:spring-boot-starter-oauth2-authorization-server")

    testImplementation(testFixtures(project(":core")))
}