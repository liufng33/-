package com.remotedata.data.remote.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitConfig {
    
    private const val DEFAULT_TIMEOUT = 30L
    
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .serializeNulls()
            .create()
    }
    
    fun provideOkHttpClient(
        enableLogging: Boolean = true,
        timeoutSeconds: Long = DEFAULT_TIMEOUT
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .readTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .writeTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
        
        if (enableLogging) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            builder.addInterceptor(loggingInterceptor)
        }
        
        return builder.build()
    }
    
    fun provideRetrofit(
        baseUrl: String,
        okHttpClient: OkHttpClient,
        gson: Gson,
        enableXml: Boolean = false
    ): Retrofit {
        val builder = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
        
        if (enableXml) {
            builder.addConverterFactory(SimpleXmlConverterFactory.create())
        }
        
        return builder.build()
    }
    
    inline fun <reified T> createService(retrofit: Retrofit): T {
        return retrofit.create(T::class.java)
    }
}
