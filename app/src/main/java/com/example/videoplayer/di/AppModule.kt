package com.example.videoplayer.di

import android.content.Context
import com.example.videoplayer.data.parser.DirectUrlParser
import com.example.videoplayer.data.parser.JsoupParser
import com.example.videoplayer.ui.player.PlayerController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideJsoupParser(): JsoupParser {
        return JsoupParser()
    }

    @Provides
    @Singleton
    fun provideDirectUrlParser(): DirectUrlParser {
        return DirectUrlParser()
    }

    @Provides
    @Singleton
    fun providePlayerController(
        @ApplicationContext context: Context
    ): PlayerController {
        return PlayerController(context)
    }
}
