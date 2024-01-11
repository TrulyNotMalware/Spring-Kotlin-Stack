package dev.notypie.dto

import java.time.Instant

class ResponseRegisteredClient(
    val clientId: String,
    val clientSecret: String,
    val clientSecretExpiresAt: Instant,
    val clientName: String
)