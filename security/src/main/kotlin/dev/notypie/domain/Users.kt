package dev.notypie.domain

import dev.notypie.jwt.dto.UserDto
import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails

@DiscriminatorColumn(name = "DTYPE")
@SequenceGenerator(
    name = "USER_SQ_GENERATOR",
    sequenceName = "USER_SEQ",
    initialValue = 1,
    allocationSize = 1
)
@Entity(name = "users")
class Users (
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_SEQ") @Column(name = "id")
    private val id: Long,

    @Column(name = "user_id", unique = true)
    private val userId: @NotBlank(message = "User id must required.") @Pattern(regexp = "[a-zA-Z0-9_-]*$") String,

    @Column(name = "user_name")
    private var userName: @NotBlank(message = "User name must required.") @Pattern(regexp = "^[a-zA-Z0-9 _-]*$") String,

    @Column(name = "email")
    private var email: @Email String,

    //10.19 Add role
    @Column(name = "role")
    private var role: String,

    @Column(name = "password")
    private val password: @NotBlank String,

    @Column(name = "phone_number")
    private var phoneNumber: @Pattern(
        regexp = "^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$",
        message = "incorrect phone number format."
    ) String,

    @Column(insertable = false, updatable = false)
    var dtype: String,

    //Address
    country: String,
    streetAddress: String,
    city: String,
    region: String,
    zipCode: String
){

    @Embedded
    private var address: Address = Address(
        country = country,
        city = city,
        streetAddress = streetAddress,
        region = region,
        zipCode = zipCode
    )

    @Embedded
    private val refreshToken: RefreshToken = RefreshToken(
        refreshToken = null, refreshAuthenticatedAt = null
    )

    fun updateUsers(updateInfo: Users): Users {
        if (userName != updateInfo.userName) userName = updateInfo.userName
        if (email != updateInfo.email) email = updateInfo.email
        if (address != updateInfo.address) address = updateInfo.address
        return this
    }

    fun createUserSecurity(): UserDetails {
        val authorities: MutableCollection<SimpleGrantedAuthority> = ArrayList()
        authorities.add(SimpleGrantedAuthority(role))
        return User(id.toString(), password, authorities)
    }

    fun toUserDto(): UserDto {
        return UserDto(
            id = id,
            userId = userId,
            email = email,
            dtype = dtype
        )
    }

    fun updateRefreshToken(newRefreshToken: String): Users {
        refreshToken.update(newRefreshToken)
        return this
    }
}