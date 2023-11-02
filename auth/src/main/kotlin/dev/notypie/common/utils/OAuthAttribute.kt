package dev.notypie.common.utils

import dev.notypie.domain.Users
import java.util.*

enum class OAuthAttribute(
    private val registrationId: String,
    private val of: (Map<String, Any>) -> Users
){
    GITHUB("github", { Users(
        userId = it["id"].toString(),
        userName = it["name"].toString(),
        email = it["email"].toString())
    }),
    MYSERVICE("myservice", {Users(
        userId = it["userId"].toString(),
        userName = it["userName"].toString(),
        email = it["email"].toString())
    });

    companion object{
        fun extract(registrationId: String, attributes: Map<String, Any>): Users
        = entries.first { registrationId == it.registrationId }.of(attributes)
    }
}