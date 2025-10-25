package com.remotedata.utils

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap

interface RateLimiter {
    suspend fun <T> execute(key: String = "default", block: suspend () -> T): T
}

class TokenBucketRateLimiter(
    private val tokensPerSecond: Double = 10.0,
    private val bucketCapacity: Int = 10
) : RateLimiter {
    
    private val buckets = ConcurrentHashMap<String, Bucket>()
    private val mutex = Mutex()
    
    override suspend fun <T> execute(key: String, block: suspend () -> T): T {
        val bucket = buckets.computeIfAbsent(key) { Bucket(bucketCapacity, tokensPerSecond) }
        
        mutex.withLock {
            bucket.refillTokens()
            
            while (bucket.tokens < 1.0) {
                val waitTime = ((1.0 - bucket.tokens) / tokensPerSecond * 1000).toLong()
                delay(waitTime.coerceAtLeast(10))
                bucket.refillTokens()
            }
            
            bucket.consumeToken()
        }
        
        return block()
    }
    
    private class Bucket(
        private val capacity: Int,
        private val refillRate: Double
    ) {
        var tokens: Double = capacity.toDouble()
        private var lastRefillTime: Long = System.currentTimeMillis()
        
        fun refillTokens() {
            val now = System.currentTimeMillis()
            val timePassed = (now - lastRefillTime) / 1000.0
            val tokensToAdd = timePassed * refillRate
            
            tokens = (tokens + tokensToAdd).coerceAtMost(capacity.toDouble())
            lastRefillTime = now
        }
        
        fun consumeToken() {
            tokens -= 1.0
        }
    }
}

class NoOpRateLimiter : RateLimiter {
    override suspend fun <T> execute(key: String, block: suspend () -> T): T = block()
}
