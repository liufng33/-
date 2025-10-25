package com.remotedata.utils

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis

class RateLimiterTest {
    
    @Test
    fun `TokenBucketRateLimiter limits execution rate`() = runTest {
        val rateLimiter = TokenBucketRateLimiter(tokensPerSecond = 5.0, bucketCapacity = 5)
        
        val time = measureTimeMillis {
            repeat(10) {
                rateLimiter.execute {
                    // Do nothing
                }
            }
        }
        
        // Should take at least 1 second to execute 10 requests at 5 req/sec
        assertTrue(time >= 900, "Time was $time ms, expected >= 900 ms")
    }
    
    @Test
    fun `TokenBucketRateLimiter allows burst requests within capacity`() = runTest {
        val rateLimiter = TokenBucketRateLimiter(tokensPerSecond = 2.0, bucketCapacity = 5)
        
        val time = measureTimeMillis {
            repeat(5) {
                rateLimiter.execute {
                    // Do nothing
                }
            }
        }
        
        // Should complete quickly since bucket has 5 tokens initially
        assertTrue(time < 500, "Time was $time ms, expected < 500 ms")
    }
    
    @Test
    fun `NoOpRateLimiter does not limit execution`() = runTest {
        val rateLimiter = NoOpRateLimiter()
        
        val time = measureTimeMillis {
            repeat(100) {
                rateLimiter.execute {
                    // Do nothing
                }
            }
        }
        
        // Should complete very quickly
        assertTrue(time < 500, "Time was $time ms, expected < 500 ms")
    }
    
    @Test
    fun `RateLimiter handles concurrent requests`() = runTest {
        val rateLimiter = TokenBucketRateLimiter(tokensPerSecond = 10.0, bucketCapacity = 10)
        val counter = mutableListOf<Int>()
        
        val jobs = (1..20).map { index ->
            async {
                rateLimiter.execute {
                    synchronized(counter) {
                        counter.add(index)
                    }
                }
            }
        }
        
        jobs.awaitAll()
        
        assertEquals(20, counter.size)
    }
    
    @Test
    fun `RateLimiter separates limits by key`() = runTest {
        val rateLimiter = TokenBucketRateLimiter(tokensPerSecond = 5.0, bucketCapacity = 5)
        
        val time = measureTimeMillis {
            val job1 = async {
                repeat(5) {
                    rateLimiter.execute("key1") {
                        // Do nothing
                    }
                }
            }
            
            val job2 = async {
                repeat(5) {
                    rateLimiter.execute("key2") {
                        // Do nothing
                    }
                }
            }
            
            awaitAll(job1, job2)
        }
        
        // Both should execute in parallel, so time should be less than sequential execution
        assertTrue(time < 1500, "Time was $time ms, expected < 1500 ms")
    }
}
