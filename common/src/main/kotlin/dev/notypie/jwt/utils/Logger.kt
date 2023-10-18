package dev.notypie.jwt.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory

// Kotlin SLF4J Logger Factory Method.
inline fun <reified T> T.logger(): Logger = LoggerFactory.getLogger(T::class.java)!!