package dev.notypie.controllers

import dev.notypie.application.RefreshTokenService
import dev.notypie.application.UserCRUDService
import dev.notypie.dto.TokenResponseDto
import dev.notypie.dto.UserRegisterDto
import dev.notypie.jwt.dto.UserDto
import jakarta.validation.Valid
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.Link
import org.springframework.hateoas.Links
import org.springframework.hateoas.MediaTypes
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/auth")
class AuthenticationController(
    private val service: UserCRUDService,
    private val refreshTokenService: RefreshTokenService
) {

    @PostMapping(value = ["/register"], produces = [MediaTypes.HAL_JSON_VALUE])
    fun register(
        @RequestBody @Valid userRegisterDto: UserRegisterDto
    ): EntityModel<UserDto>{
        val userResponseDto = this.service.register(userRegisterDto = userRegisterDto)
        val selfLink: Link = linkTo(
            methodOn(AuthenticationController::class.java).register(userRegisterDto)
        ).withSelfRel()
        val allLinks: Links = Links.of(selfLink)
        return EntityModel.of(userResponseDto, allLinks)
    }

    @GetMapping(value = ["/reissue"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun reissueAccessToken(
        @RequestHeader(HttpHeaders.AUTHORIZATION) originalAccessToken: String,
        @CookieValue("refresh-token") refreshToken: String
    ): ResponseEntity<TokenResponseDto>{
        val accessToken = originalAccessToken.replace("Bearer ", "")
        val reissueToken = this.refreshTokenService.refreshJwtToken(accessToken = accessToken, refreshToken = refreshToken)
        val responseCookie = this.refreshTokenService.createRefreshToken(refreshToken = refreshToken)
        return ResponseEntity.status(HttpStatus.OK)
            .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
            .body(TokenResponseDto.toTokenResponseDto(reissueToken))
    }
}