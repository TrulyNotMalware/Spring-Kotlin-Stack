package dev.notypie.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import dev.notypie.application.RefreshTokenService
import dev.notypie.application.UserCRUDService
import dev.notypie.base.annotations.ControllerTest
import dev.notypie.builders.UserRegisterDtoBuilder
import dev.notypie.configurations.SecurityConfiguration
import dev.notypie.dto.UserRegisterDto
import dev.notypie.exchanger.UserInfoExchanger
import dev.notypie.jwt.dto.JwtDto
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.hateoas.MediaTypes
import org.springframework.http.MediaType
import org.springframework.http.ResponseCookie
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*


@ControllerTest
@WebMvcTest(controllers = [AuthenticationController::class])
@Import(SecurityConfiguration::class)//WebSecurityCustomizer import.
class AuthenticationControllerTest @Autowired constructor(
    private val objectMapper: ObjectMapper,
    private val mockMvc: MockMvc,
    @MockkBean val userService: UserCRUDService,
    @MockkBean val refreshTokenService: RefreshTokenService
): BehaviorSpec(){

    init {
        given("[app.Auth] Validation Success"){
            val accessToken = "test-access-token"
            val refreshToken = "test-refresh-token"
            //Prepare data
            val dtoBuilder = UserRegisterDtoBuilder()
            val register: UserRegisterDto = dtoBuilder.build()
            val user = UserInfoExchanger.exchangeToUsers(register)

            val jwtDto = JwtDto(refreshToken = refreshToken, accessToken = accessToken, accessTokenExpiredDate = Date())
            val responseCookie = ResponseCookie.from("refresh-token", refreshToken)
                .httpOnly(true).secure(true).path("/").maxAge("20000".toLong()).build()

            //Mocking bean
            every { userService.register(any(UserRegisterDto::class)) } returns user.toUserDto()
            every { refreshTokenService.refreshJwtToken(any(String::class), any(String::class))} returns jwtDto
            every { refreshTokenService.createRefreshToken(any(String::class))} returns responseCookie

            `when`("Incorrect user value"){
                val incorrectUsers: MutableList<UserRegisterDto> = mutableListOf()
                //Create Incorrect input users.
                val userName: String = dtoBuilder.userName
                dtoBuilder.userName = "#%*@%#(*@$!&$#%#"
                incorrectUsers.add(dtoBuilder.build())

                val userId = dtoBuilder.userId
                dtoBuilder.userName = userName
                dtoBuilder.userId = "1#%%#%%#%*!$"
                incorrectUsers.add(dtoBuilder.build())

                val email: String = dtoBuilder.email
                dtoBuilder.userId = userId
                dtoBuilder.email = "!_!)!@test.mail"
                incorrectUsers.add(dtoBuilder.build())

                dtoBuilder.email = email
                dtoBuilder.password = "SELECT * FROM USERS"
                incorrectUsers.add(dtoBuilder.build())

                then("Failed to register"){
                    //when & then
                    for (incorrectUser in incorrectUsers) {
                        mockMvc.perform(
                            post("/api/auth/register")
                                .with(csrf())
                                .accept(MediaTypes.HAL_JSON_VALUE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(incorrectUser))
                        ).andExpect(status().isBadRequest())
                    }

                }
            }
        }
    }
}