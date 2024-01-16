package dev.notypie.domain

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import java.io.Serializable
import java.util.*

@Entity(name = "authorization_consent")
@IdClass(AuthorizationConsent.AuthorizationConsentId::class)
class AuthorizationConsent(
    @field:Id
    val registeredClientId: String,

    @field:Id
    val principalName: String,

) {

    class AuthorizationConsentId(
        private val registeredClientId: String,
        private val principleName: String,
    ): Serializable{
        companion object{
            @JvmStatic private val serialVersionUID = 8683608026915663539L
        }
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || javaClass != other.javaClass) return false
            val that = other as AuthorizationConsentId
            return registeredClientId == that.registeredClientId && principleName == that.principleName
        }

        override fun hashCode(): Int = Objects.hash(registeredClientId, principleName)
    }
}