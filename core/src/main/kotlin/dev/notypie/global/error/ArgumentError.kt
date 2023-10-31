package dev.notypie.global.error

class ArgumentError(
    private val fieldName: String,
    private val value: String,
    private val reason: String
)