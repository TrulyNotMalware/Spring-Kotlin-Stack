package dev.notypie.cache

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*
import java.util.concurrent.TimeUnit

@EnableCaching
@Configuration
class CacheConfiguration {

    @Bean
    fun cacheManager(): CacheManager{
        val caches: List<CaffeineCache> = Arrays.stream(CacheType.values())
            .map { cacheType ->
                CaffeineCache(
                    cacheType.cacheName,
                    Caffeine.newBuilder().recordStats()
                        .expireAfterWrite(cacheType.expiredTimeSeconds.toLong(), TimeUnit.SECONDS)
                        .maximumSize(cacheType.cacheMaxSize.toLong()).build()
                )
            }.toList()
        val caffeineCacheManager = SimpleCacheManager()
        caffeineCacheManager.setCaches(caches)
        return caffeineCacheManager
    }
}