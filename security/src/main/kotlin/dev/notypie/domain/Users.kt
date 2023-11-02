package dev.notypie.domain

import dev.notypie.constants.Constants
import dev.notypie.jwt.dto.UserDto
import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails

@SequenceGenerator(
    name = "USER_SQ_GENERATOR",
    sequenceName = "USER_SEQ",
    initialValue = 1,
    allocationSize = 1
)
@Entity(name = "users")
class Users (
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_SEQ") @Column(name = "id")
    val id: Long = 0L,

    @Column(name = "user_id", unique = true)
    val userId: @NotBlank(message = "User id must required.") @Pattern(regexp = "[a-zA-Z0-9_-]*$") String,

    @Column(name = "user_name")
    private var userName: @NotBlank(message = "User name must required.") @Pattern(regexp = "^[a-zA-Z0-9 _-]*$") String,

    @Column(name = "email")
    private val email: @Email String,

    //10.19 Add role
    @Column(name = "role")
    private var role: String = Constants.USER_ROLE_DEFAULT,

    //If Password is null, it means is OAuthUser.
    @Column(name = "password")
    val password: @NotBlank String = "OAuthUser",

    @Column(name = "phone_number")
    private var phoneNumber: @Pattern(
        regexp = "^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$",
        message = "incorrect phone number format."
    ) String? = null,

    @Embedded
    //@Embedded class objects can be null when all properties are null.
    private var address: Address? = Address(
        country = null,
        city = null,
        streetAddress = null,
        region = null,
        zipCode = null
    ),

    @Embedded
    //@Embedded class objects can be null when all properties are null.
    private var refreshToken: RefreshToken? = RefreshToken(
        refreshToken = null, refreshAuthenticatedAt = null
    ),

    @Column(insertable = false, updatable = false)
    var dtype: String = "user",

    ){

    fun updateUsers(updateInfo: Users): Users {
        if(this.address == null) this.address = Address() // Null Check.
        if (this.userName != updateInfo.userName) this.userName = updateInfo.userName
        // Now email is cannot update.
//        if (email != updateInfo.email) email = updateInfo.email
        if(updateInfo.refreshToken == null ) return this
        if (this.address != updateInfo.address) this.address!!.update(updateInfo.address!!) // Never become null.
        return this
    }

    fun createUserSecurity(): UserDetails {
        val authorities: MutableCollection<SimpleGrantedAuthority> = ArrayList()
        authorities.add(SimpleGrantedAuthority(role))
        return User(id.toString(), password, authorities)
    }

    fun toUserDto(): UserDto = UserDto(
            id = id,
            userId = userId,
            email = email,
            dtype = dtype
        )


    fun updateRefreshToken(newRefreshToken: String?): Users {
//        this.refreshToken.let {  }
        if(this.refreshToken == null) this.refreshToken = RefreshToken()
        this.refreshToken!!.update(newRefreshToken)
        return this
    }
    //FIXME this functions is correct?
    fun getRefreshToken(): String? = this.refreshToken?.getRefreshToken()
    fun getRole(): String = this.role
}