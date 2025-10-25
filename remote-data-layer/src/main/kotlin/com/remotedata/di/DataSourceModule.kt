package com.remotedata.di

import com.remotedata.data.remote.datasource.*
import com.remotedata.data.remote.parser.HtmlFetcher
import com.remotedata.data.remote.parser.HtmlParser
import com.remotedata.data.remote.parser.JsoupHtmlFetcher
import com.remotedata.data.remote.parser.JsoupHtmlParser
import com.remotedata.utils.RateLimiter
import com.remotedata.utils.TokenBucketRateLimiter
import org.koin.dsl.module

val dataSourceModule = module {
    
    single<RateLimiter> {
        TokenBucketRateLimiter(tokensPerSecond = 10.0, bucketCapacity = 10)
    }
    
    single<HtmlParser> {
        JsoupHtmlParser()
    }
    
    single<HtmlFetcher> {
        JsoupHtmlFetcher()
    }
    
    single<ApiRemoteDataSource> {
        ApiRemoteDataSourceImpl(
            apiService = get(),
            rateLimiter = get()
        )
    }
    
    single<SearchRemoteDataSource> {
        SearchRemoteDataSourceImpl(
            searchService = get(),
            rateLimiter = get()
        )
    }
    
    single<ParserRemoteDataSource> {
        ParserRemoteDataSourceImpl(
            htmlFetcher = get(),
            defaultParser = get(),
            rateLimiter = get()
        )
    }
    
    single<DynamicApiDataSource> {
        DynamicApiDataSourceImpl(
            dynamicApiService = get(),
            rateLimiter = get()
        )
    }
}
