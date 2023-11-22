package dev.notypie.builders

import dev.notypie.dto.UserRegisterDto

class UserRegisterDtoBuilder(
    var userId: String = "testUserId",
    var userName: String = "testUserName",
    var email: String = "thisistest@test.email",
    var password: String = "testPassword",
    var phoneNumber: String = "010-1234-5678",
    var country: String = "testCountry",
    var streetAddress: String = "testStreetAddress",
    var city: String = "testCity",
    var region: String = "testRegion",
    var zipCode: String = "testZipCode"
) {

    fun build():UserRegisterDto =
        UserRegisterDto(
            userId = userId,
            userName = userName,
            email = email,
            password = password,
            phoneNumber = phoneNumber,
            country = country,
            streetAddress = streetAddress,
            city = city,
            region = region,
            zipCode = zipCode
        )
}