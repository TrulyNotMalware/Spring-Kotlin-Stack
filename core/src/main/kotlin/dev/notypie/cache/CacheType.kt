package dev.notypie.cache

import dev.notypie.constants.CacheConstants


enum class CacheType(
    val cacheName: String,
    val expiredTimeSeconds: Int,
    val cacheMaxSize:Int,
) {
    //1day cache
    DEFAULT(CacheConstants.DEFAULT_CACHE, 60 * 60 * 24, 1);
}