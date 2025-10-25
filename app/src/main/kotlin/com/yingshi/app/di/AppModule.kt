package com.yingshi.app.di

import com.remotedata.data.remote.api.ApiService
import com.remotedata.data.remote.api.DynamicApiService
import com.remotedata.data.remote.api.RetrofitConfig
import com.remotedata.data.remote.api.SearchService
import com.remotedata.data.remote.datasource.ApiRemoteDataSource
import com.remotedata.data.remote.datasource.DynamicApiDataSource
import com.remotedata.data.remote.datasource.ParserRemoteDataSource
import com.remotedata.data.remote.datasource.SearchRemoteDataSource
import com.remotedata.data.remote.parser.HtmlFetcher
import com.remotedata.data.remote.parser.HtmlParser
import com.remotedata.data.remote.parser.RuleBasedParser
import com.remotedata.utils.RateLimiter
import com.remotedata.utils.TokenBucketRateLimiter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return RetrofitConfig.createOkHttpClient()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return RetrofitConfig.createRetrofit(
            baseUrl = "https://132130.v.nxog.top/",
            okHttpClient = okHttpClient
        )
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideSearchService(retrofit: Retrofit): SearchService {
        return retrofit.create(SearchService::class.java)
    }

    @Provides
    @Singleton
    fun provideDynamicApiService(okHttpClient: OkHttpClient): DynamicApiService {
        return RetrofitConfig.createDynamicApiService(okHttpClient)
    }

    @Provides
    @Singleton
    fun provideRateLimiter(): RateLimiter {
        return TokenBucketRateLimiter(
            tokensPerSecond = 10.0,
            bucketCapacity = 10
        )
    }

    @Provides
    @Singleton
    fun provideHtmlParser(): HtmlParser {
        return HtmlParser()
    }

    @Provides
    @Singleton
    fun provideRuleBasedParser(): RuleBasedParser {
        return RuleBasedParser()
    }

    @Provides
    @Singleton
    fun provideHtmlFetcher(okHttpClient: OkHttpClient): HtmlFetcher {
        return HtmlFetcher(okHttpClient)
    }

    @Provides
    @Singleton
    fun provideApiRemoteDataSource(
        apiService: ApiService,
        rateLimiter: RateLimiter
    ): ApiRemoteDataSource {
        return ApiRemoteDataSource(apiService, rateLimiter)
    }

    @Provides
    @Singleton
    fun provideSearchRemoteDataSource(
        searchService: SearchService,
        rateLimiter: RateLimiter
    ): SearchRemoteDataSource {
        return SearchRemoteDataSource(searchService, rateLimiter)
    }

    @Provides
    @Singleton
    fun provideParserRemoteDataSource(
        htmlFetcher: HtmlFetcher,
        ruleBasedParser: RuleBasedParser,
        rateLimiter: RateLimiter
    ): ParserRemoteDataSource {
        return ParserRemoteDataSource(htmlFetcher, ruleBasedParser, rateLimiter)
    }

    @Provides
    @Singleton
    fun provideDynamicApiDataSource(
        dynamicApiService: DynamicApiService,
        rateLimiter: RateLimiter
    ): DynamicApiDataSource {
        return DynamicApiDataSource(dynamicApiService, rateLimiter)
    }
}
