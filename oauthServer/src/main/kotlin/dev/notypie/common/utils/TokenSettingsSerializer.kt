package dev.notypie.common.utils

import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm
import org.springframework.security.oauth2.server.authorization.settings.ConfigurationSettingNames
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings
import org.springframework.util.Assert
import java.lang.Boolean
import java.time.Duration
import java.util.*
import kotlin.Any
import kotlin.Double
import kotlin.IllegalArgumentException
import kotlin.String


class TokenSettingsSerializer {
    private val tokenSettings: TokenSettings

    constructor(tokenSettings: TokenSettings){
        this.tokenSettings = this.buildTokenSettings(tokenSettings.settings)
    }

    constructor(setting: Map<String, String>){
        this.tokenSettings = this.buildTokenSettings(setting)
    }
    /**
     * Fixed an issue that did not serialize correctly when reading values from a database.
     * 1. [Double][java.lang.Double] type to [Duration][java.time.Duration]
     * 2. [Map][java.util.Map] type to [OAuth2TokenFormat][org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat] instance.
     * @param settings the [Map][java.util.Map] object from database.
     * @return [TokenSettings][org.springframework.security.oauth2.server.authorization.settings.TokenSettings] object.
     */
    private fun buildTokenSettings(settings: Map<String, Any>): TokenSettings {
        return TokenSettings.builder() //Convert Duration type.
            .authorizationCodeTimeToLive(
                this.durationConverter(getSetting(ConfigurationSettingNames.Token.AUTHORIZATION_CODE_TIME_TO_LIVE,settings))
            )
            .accessTokenTimeToLive(
                this.durationConverter(getSetting(ConfigurationSettingNames.Token.ACCESS_TOKEN_TIME_TO_LIVE, settings))
            )
            .deviceCodeTimeToLive(
                this.durationConverter(getSetting(ConfigurationSettingNames.Token.DEVICE_CODE_TIME_TO_LIVE, settings))
            )
            .refreshTokenTimeToLive(
                this.durationConverter(getSetting(ConfigurationSettingNames.Token.REFRESH_TOKEN_TIME_TO_LIVE, settings))
            ) //Others
            .reuseRefreshTokens(
                Boolean.TRUE == getSetting<Any>(ConfigurationSettingNames.Token.REUSE_REFRESH_TOKENS, settings)
            )
            .idTokenSignatureAlgorithm(
                SignatureAlgorithm.from(
                    getSetting(
                        ConfigurationSettingNames.Token.ID_TOKEN_SIGNATURE_ALGORITHM,
                        settings
                    )
                )
            )
            .accessTokenFormat(
                this.tokenFormatConverter(
                    getSetting(ConfigurationSettingNames.Token.ACCESS_TOKEN_FORMAT, settings),
                    null
                )
            )
            .build()
    }

    /**
     * Convert double value to Duration type.
     * change Double value to Duration of seconds.
     * @param value
     * The [Double][java.lang.Double] value.
     * @return [duration][java.time.Duration] instance.
     */
    private fun durationConverter(value: Double): Duration = Duration.ofSeconds(Math.round(value))


    /**
     * Cast the [Map][java.util.Map] instance to [OAuth2TokenFormat][org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat] object.
     * this function throws [IllegalArgumentException][java.lang.IllegalArgumentException] when cannot type cast.
     * @param map the data object
     * @param keyName Nullable string key name. the default key name is "value", but you could also change this.
     * @see IllegalArgumentException
     *
     * @return [OAuth2TokenFormat][org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat] instance.
     */
    private fun tokenFormatConverter(map: Map<String, Any>, keyName: String?): OAuth2TokenFormat {
        //in my case, value from database is LinkedHashMap.
        Assert.notEmpty(map, "Map object is empty.")
        val key: String = keyName ?: "value"
        if (OAuth2TokenFormat.SELF_CONTAINED.value == getSetting(key, map)) return OAuth2TokenFormat.SELF_CONTAINED
        else if (OAuth2TokenFormat.REFERENCE.value == getSetting(key, map)) return OAuth2TokenFormat.REFERENCE
        throw IllegalArgumentException("Cannot convert " + getSetting(key, map) + "to OAuth2TokenFormat.")
    }

    /**
     * get value from Map object. this function throws IllegalArgumentException.
     * @see IllegalArgumentException
     *
     * @param name The key name for extract from map.
     * @param settings the Map object.
     * @param <T> Return type.
    </T> */
    private fun <T> getSetting(name: String, settings: Map<String, Any>): T {
        Assert.hasText(name, "name cannot be empty")
        Assert.notEmpty(settings, "Map object is empty.")
        Assert.notNull(settings[name], "Value not exist.")
        return settings[name] as T
    }
}