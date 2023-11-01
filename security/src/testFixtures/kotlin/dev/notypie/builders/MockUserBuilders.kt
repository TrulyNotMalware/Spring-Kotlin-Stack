package dev.notypie.builders

import dev.notypie.domain.Address
import dev.notypie.domain.RefreshToken
import dev.notypie.domain.Users
import dev.notypie.jwt.dto.LoginRequestDto

class MockUserBuilders (
    val id: Long = MockUserBuilders.id,
    val userId: String = MockUserBuilders.userId,
    val userName: String = MockUserBuilders.userName,
    val email: String = MockUserBuilders.email,
    val password: String = MockUserBuilders.password,
    val phoneNumber: String = MockUserBuilders.phoneNumber,
    val country: String? = MockUserBuilders.country,
    val streetAddress: String? = MockUserBuilders.streetAddress,
    val city: String? = MockUserBuilders.city,
    val region: String? = MockUserBuilders.region,
    val zipCode: String? = MockUserBuilders.zipCode
){
    companion object{
        const val id = 1L
        const val userId = "testUserId"
        const val userName = "testUserName"
        const val email = "thisistest@test.email"
        const val password = "testPassword"
        const val phoneNumber = "010-1234-5678"
        const val country = "testCountry"
        const val streetAddress = "testStreetAddress"
        const val city = "testCity"
        const val region = "testRegion"
        const val zipCode = "testZipCode"
    }

    fun createDefaultUsers(): Users
    = Users(
        userId = this.userId,
        userName = this.userName,
        email = this.email,
        password = this.password,
        phoneNumber = this.phoneNumber,
        address = Address(
            country = this.country,
            streetAddress = this.streetAddress,
            city = this.city,
            region = this.region,
            zipCode = this.zipCode
        ),
        refreshToken = RefreshToken(
            refreshToken = null,
            refreshAuthenticatedAt = null
        ),
        dtype = "testDtype"
    )

    fun exchange(target: Users) : LoginRequestDto
    = LoginRequestDto(
        userId = target.userId,
        password = target.password
    )
}