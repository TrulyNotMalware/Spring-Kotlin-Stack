package dev.notypie.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import java.io.Serial
import java.io.Serializable
import java.util.*

@Entity(name = "authorization_consent")
@IdClass(AuthorizationConsent.AuthorizationConsentId::class)
class AuthorizationConsent(
    @field:Id
    val registeredClientId: String,

    @field:Id
    val principalName: String,

    @field:Column(length = 1000)
    val authorities: String
) {

    class AuthorizationConsentId(
        private val registeredClientId: String,
        private val principalName: String,
    ): Serializable{
        companion object{
            @Serial @JvmStatic private val serialVersionUID = 8683608026915663539L
        }
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || javaClass != other.javaClass) return false
            val that = other as AuthorizationConsentId
            return registeredClientId == that.registeredClientId && principalName == that.principalName
        }

        override fun hashCode(): Int = Objects.hash(registeredClientId, principalName)
    }
}