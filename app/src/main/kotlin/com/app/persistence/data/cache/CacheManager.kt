package com.app.persistence.data.cache

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

data class CacheEntry<T>(
    val data: T,
    val timestamp: Long,
    val ttl: Long
) {
    fun isExpired(): Boolean {
        return System.currentTimeMillis() - timestamp > ttl
    }
}

@Singleton
class CacheManager @Inject constructor() {
    private val cache = ConcurrentHashMap<String, CacheEntry<Any>>()
    private val mutex = Mutex()
    
    suspend fun <T> get(key: String): T? {
        return mutex.withLock {
            @Suppress("UNCHECKED_CAST")
            val entry = cache[key] as? CacheEntry<T>
            if (entry?.isExpired() == true) {
                cache.remove(key)
                null
            } else {
                entry?.data
            }
        }
    }
    
    suspend fun <T> put(key: String, value: T, ttl: Long = DEFAULT_TTL) {
        mutex.withLock {
            cache[key] = CacheEntry(
                data = value as Any,
                timestamp = System.currentTimeMillis(),
                ttl = ttl
            )
        }
    }
    
    suspend fun invalidate(key: String) {
        mutex.withLock {
            cache.remove(key)
        }
    }
    
    suspend fun invalidateAll() {
        mutex.withLock {
            cache.clear()
        }
    }
    
    suspend fun invalidatePattern(pattern: String) {
        mutex.withLock {
            val regex = Regex(pattern)
            cache.keys.filter { regex.matches(it) }.forEach { cache.remove(it) }
        }
    }
    
    companion object {
        const val DEFAULT_TTL = 5 * 60 * 1000L // 5 minutes
        const val SHORT_TTL = 1 * 60 * 1000L // 1 minute
        const val LONG_TTL = 30 * 60 * 1000L // 30 minutes
    }
}
