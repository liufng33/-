package com.remotedata.di

import com.google.gson.Gson
import com.remotedata.data.remote.api.*
import okhttp3.OkHttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

val networkModule = module {
    
    single { RetrofitConfig.provideGson() }
    
    single { RetrofitConfig.provideOkHttpClient(enableLogging = true) }
    
    single(named("main")) {
        RetrofitConfig.provideRetrofit(
            baseUrl = "https://132130.v.nxog.top/",
            okHttpClient = get(),
            gson = get(),
            enableXml = true
        )
    }
    
    single(named("dynamic")) {
        RetrofitConfig.provideRetrofit(
            baseUrl = "https://example.com/",
            okHttpClient = get(),
            gson = get(),
            enableXml = false
        )
    }
    
    single<ApiService> {
        RetrofitConfig.createService(get(named("main")))
    }
    
    single<SearchService> {
        RetrofitConfig.createService(get(named("main")))
    }
    
    single<DynamicApiService> {
        RetrofitConfig.createService(get(named("dynamic")))
    }
}
