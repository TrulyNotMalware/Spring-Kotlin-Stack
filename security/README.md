# Security Module
The '*Security*' module offers default user domain entities and repositories, along with basic components and services for "jwt" (JSON Web Tokens), which are the default security settings. ( If you wish to use these features, you can activate them by specifying the profile name as "jwt" ). And, it provides OAuth-related beans and configurations that can be activated as needed.

## Included
- JWT Modules with RSA
- Default User Entity & Repository

## Getting Started
### Required Dependencies
- JPA & Database
```kotlin
dependencies {
    // Required
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    //Select the appropriate dependencies based on your database.
    //For example, if you are using MariaDB...
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
    runtimeOnly("mysql:mysql-connector-java")

    //Oracle 19c examples
    runtimeOnly("com.oracle.database.jdbc:ojdbc8:19.8.0.0")
    implementation("com.oracle.database.jdbc:ucp:19.8.0.0")
    implementation("com.oracle.database.security:oraclepki")
    implementation ("com.oracle.database.security:osdt_core")
    implementation ("com.oracle.database.security:osdt_cert")
}
```
JPA & Database related dependencies are necessary as the module includes repositories for basic User entities.
- Validation
```kotlin
dependencies {
    // Validate entity.
    implementation("org.springframework.boot:spring-boot-starter-validation")
}
```
A more easy way is to include the '[core](https://github.com/TrulyNotMalware/Spring-Kotlin-Stack/blob/main/core/README.md)' projects.
### Configurations
This module uses RSA authentication, so you have to generate your own key. If you don't have any key, check [generateToken.sh](https://github.com/TrulyNotMalware/Spring-Stack/blob/main/security/src/main/resources/generateToken.sh) for token generation.  
If you prepared the key, place the keystore in the resource directory and proceed to complete the following settings.
```yaml
spring:
  config:
    activate:
      on-profile: "jwt-configuration"

jwt:
  token:
    accessTokenExpiredTime: ACCESS_EXPIRED_TIME
    refreshTokenExpiredTime: REFRESH_EXPIRED_TIME
    keystore:
      classpath: YOUR_KEY_NAME.jks
      password: YOUR_PASSWORD
    key:
      alias: YOUR_ALIAS
      privateKeyPassPhrase: YOUR_PASS_PHRASE

authentication:
   login:
      requestUrl: "/api/auth/login"
   logout:
      requestUrl: "/api/auth/user/logout"
---
spring:
  config:
    activate:
      on-profile: "database-setup"
  datasource:
    driver-class-name: ${YOUR_DATABASE_DRIVER}
    url: ${YOUR_DATABASE_URL}
    username: ${YOUR_DATABASE_USER_NAME}
    password: ${YOUR_DATABASE_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: ${SETUP_DDL_AUTO}
```
For example,
```bash
#!/bin/bash
# Example key file.
keytool -genkeypair -keystore exampleKey.jks -alias myAlias -keyalg rsa -storepass mypassword
```
Include the generated example.jks key file into the resource directory, and then update the setup file as follows.
```yaml
spring:
  config:
    activate:
      on-profile: "jwt-configuration"

jwt:
  token:
    accessTokenExpiredTime: 200000
    refreshTokenExpiredTime: 2000000
    keystore:
      classpath: example.jks
      password: mypassword
    key:
      alias: myAlias
      privateKeyPassPhrase: mypassword
```
### Usage
When you build your application with this modules, basic User Entities and Repository are already provided, so the simple way to creating user authentication is implement the *UsernamePasswordAuthenticationFilter*.  
Here is example.
```kotlin
@Component
class LoginAuthenticationFilter(
   ...
   private val refreshTokenService: RefreshTokenService,
   ...
): UsernamePasswordAuthenticationFilter() {
    ...
   override fun successfulAuthentication(
      request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain, authResult: Authentication
   ) {
      val user = authResult.principal as User
      ...
      //example 1, Reject duplicated login.
      if(this.refreshTokenService.isDuplicateRefreshToken(id = id)){
         this.refreshTokenService.updateRefreshToken(id, null)
         throw UserDomainException(UserErrorCodeImpl.AUTHENTICATION_FAILED)
      }
      ...
      //example 2, Generate new Tokens when successfully authenticated.
      val newToken = this.refreshTokenService.generateNewTokens(id = id, roles = roles)
      this.refreshTokenService.updateRefreshToken(id = id, newToken.refreshToken)
      val refreshTokenCookie = refreshTokenService.createRefreshToken(newToken.refreshToken)
      val cookie = of(refreshTokenCookie)
      
      response.contentType = MediaType.APPLICATION_JSON_VALUE
      response.addCookie(cookie)
      
      val tokens = mapOf<String, Any>(
         "id" to id,
         "accessToken" to newToken.accessToken,
         "expiredTime" to SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(newToken.accessTokenExpiredDate),
         "message" to "login success"
      )
      objectMapper.writeValue(response.outputStream, tokens)
   }
}
```
Of course, it can be implemented in a different way. check core feature [RefreshTokenService](https://github.com/TrulyNotMalware/Spring-Stack/blob/main/security/src/main/java/dev/notypie/application/RefreshTokenService.java) and make it what you want.  
If you want some more examples, check the [auth](https://github.com/TrulyNotMalware/Modules/blob/main/auth/README.md) application.

## Customize
The primary key must be written in **Long** type. Except for this, You are free to modify the User entity as needed.
```kotlin
@Entity
class Member(
   @Id
   private val memberId: Long,
   
   @Column(name = "user_name")
   private val memberName: String
)
```
However, if you decide to make changes to the user entity, please be aware that you will need to customize the following two components.
1. Repository for user entity.
2. RefreshTokenService implements.  
   Implement a new Refresh Token Service for Token CRUD operations, reissue accessToken and incoming tasks.
```kotlin
class MyRefreshTokenService : RefreshTokneService {
    //Update refresh token in db.
    override fun updateRefreshToken(id: Long, refreshToken: String?) = ...
    //Verify the token already exists in the database.
    override fun isDuplicateRefreshToken(id: Long): Boolean = ...
    //Reissue access token.
    override fun refreshJwtToken(accessToken: String, refreshToken: String): JwtDto = ...
    //Generate new Tokens( Access , Refresh )
    override fun generateNewTokens(id: Long, roles: List<String>): JwtDto = ...
    ...
}
```
---
# Security Module
'*Security*' 모듈은 기본 보안 설정인 'jwt'(JSON Web Token)에 대한 기본 구성 및 서비스와 함께 기본 사용자 도메인 엔티티 및 Repository를 제공합니다. (프로파일 이름을 'jwt'로 지정하여 활성화할 수 있습니다.) 또한 필요에 따라 활성화할 수 있는 OAuth 관련 Beans 및 구성을 제공합니다.

## Included
- RSA 인증방식 JWT 모듈
- 기본적인 User Entity 와 Repository.

## Getting Started
### 의존성 설정
- JPA & Database
```kotlin
dependencies {
   // Required
   implementation("org.springframework.boot:spring-boot-starter-data-jpa")

   //데이터베이스에 따라 적절한 종속성을 선택하세요.
   //ex1) Mariadb
   runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
   runtimeOnly("mysql:mysql-connector-java")

   //ex2) OracleDatabase 19c
   runtimeOnly("com.oracle.database.jdbc:ojdbc8:19.8.0.0")
   implementation("com.oracle.database.jdbc:ucp:19.8.0.0")
   implementation("com.oracle.database.security:oraclepki")
   implementation ("com.oracle.database.security:osdt_core")
   implementation ("com.oracle.database.security:osdt_cert")
}
```
모듈에 기본 User 엔티티와 Repository가 포함되어 있으므로 JPA 및 Database 관련 종속성이 필요합니다.
- Validation
```kotlin
dependencies {
    // 엔티티 정합성 검증을 위해 사용됩니다.
    implementation("org.springframework.boot:spring-boot-starter-validation")
}
```
더 쉬운 방법으로는 '[core](https://github.com/TrulyNotMalware/Modules/blob/main/core/README.md)' 프로젝트를 포함하면 됩니다.
### 어플리케이션 설정
이 모듈은 RSA 인증을 사용하므로 비대칭 키를 생성해야 합니다. 키가 없는 경우에는 [generateToken.sh ](https://github.com/TrulyNotMalware/Spring-Stack/blob/main/security/src/main/resources/generateToken.sh)스크립트에서 토큰을 생성하는 방법을 확인하세요.  
키를 준비했으면 키 저장소를 Resource 디렉토리에 두고 Application 설정을 작성합니다.
```yaml
spring:
  config:
    activate:
      on-profile: "jwt-configuration"

jwt:
  token:
    accessTokenExpiredTime: ACCESS_EXPIRED_TIME
    refreshTokenExpiredTime: REFRESH_EXPIRED_TIME
    keystore:
      classpath: YOUR_KEY_NAME.jks
      password: YOUR_PASSWORD
    key:
      alias: YOUR_ALIAS
      privateKeyPassPhrase: YOUR_PASS_PHRASE

authentication:
   login:
      requestUrl: "/api/auth/login"
   logout:
      requestUrl: "/api/auth/user/logout"
---
spring:
  config:
    activate:
      on-profile: "database-setup"
  datasource:
    driver-class-name: ${YOUR_DATABASE_DRIVER}
    url: ${YOUR_DATABASE_URL}
    username: ${YOUR_DATABASE_USER_NAME}
    password: ${YOUR_DATABASE_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: ${SETUP_DDL_AUTO}
```
예를 들어,
```bash
#!/bin/bash
# 예제 키 생성
keytool -genkeypair -keystore exampleKey.jks -alias myAlias -keyalg rsa -storepass mypassword
```
생성된 'example.jks' 파일을 resource 디렉터리 아래에 위치시키고, 설정 파일에 다음과 같이 작성합니다.
```yaml
spring:
   config:
      activate:
         on-profile: "jwt-configuration"

jwt:
   token:
      accessTokenExpiredTime: 200000
      refreshTokenExpiredTime: 2000000
      keystore:
         classpath: example.jks
         password: mypassword
      key:
         alias: myAlias
         privateKeyPassPhrase: mypassword
```
### 기본 사용법
기본 User Entities 및 Repository가 이미 제공되므로, 이 모듈로 Application을 구축할 때 사용자 인증을 만드는 간단한 방법은 *UsernamePasswordAuthenticationFilter*를 구현하는 것입니다.
```kotlin
@Component
class LoginAuthenticationFilter(
   ...
   private val refreshTokenService: RefreshTokenService,
   ...
): UsernamePasswordAuthenticationFilter() {
   ...
   override fun successfulAuthentication(
      request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain, authResult: Authentication
   ) {
      val user = authResult.principal as User
      ...
      //example 1, Reject duplicated login.
      if(this.refreshTokenService.isDuplicateRefreshToken(id = id)){
         this.refreshTokenService.updateRefreshToken(id, null)
         throw UserDomainException(UserErrorCodeImpl.AUTHENTICATION_FAILED)
      }
      ...
      //example 2, Generate new Tokens when successfully authenticated.
      val newToken = this.refreshTokenService.generateNewTokens(id = id, roles = roles)
      this.refreshTokenService.updateRefreshToken(id = id, newToken.refreshToken)
      val refreshTokenCookie = refreshTokenService.createRefreshToken(newToken.refreshToken)
      val cookie = of(refreshTokenCookie)

      response.contentType = MediaType.APPLICATION_JSON_VALUE
      response.addCookie(cookie)

      val tokens = mapOf<String, Any>(
         "id" to id,
         "accessToken" to newToken.accessToken,
         "expiredTime" to SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(newToken.accessTokenExpiredDate),
         "message" to "login success"
      )
      objectMapper.writeValue(response.outputStream, tokens)
   }
}
```
물론 다른 방식으로 구현할 수도 있습니다. 토큰 관련 핵심 역할을 하는 [RefreshTokenService](https://github.com/TrulyNotMalware/Spring-Stack/blob/main/security/src/main/java/dev/notypie/application/RefreshTokenService.java) 을 확인하고 원하는 대로 만들어 보세요.  
더 많은 예시는 [auth](https://github.com/TrulyNotMalware/Modules/blob/main/auth/README.md) Application을 확인하세요.

## 커스터마이징
Priamry Key 는 **Long** 타입으로 작성되어야 합니다. 이를 제외하면 나머지는 필요에 따라 엔티티를 자유롭게 수정할 수 있습니다.
```kotlin
@Entity
class Member(
   @Id
   private val memberId: Long,
   
   @Column(name = "user_name")
   private val memberName: String
)
```
그러나 user 엔티티를 변경한 경우, 아래 두 가지 구성 요소를 함께 변경해야 합니다.
1. User Repository 구현.
2. RefreshTokenService 구현.  
   토큰 CRUD 작업, AccessToken 을 갱신하는 작업을 하는 새로운 RefreshToken 서비스를 구현해야 합니다.
```kotlin
class MyRefreshTokenService : RefreshTokneService {
   //Update refresh token in db.
   override fun updateRefreshToken(id: Long, refreshToken: String?) = ...
   //Verify the token already exists in the database.
   override fun isDuplicateRefreshToken(id: Long): Boolean = ...
   //Reissue access token.
   override fun refreshJwtToken(accessToken: String, refreshToken: String): JwtDto = ...
   //Generate new Tokens( Access , Refresh )
   override fun generateNewTokens(id: Long, roles: List<String>): JwtDto = ...
   ...
}
```