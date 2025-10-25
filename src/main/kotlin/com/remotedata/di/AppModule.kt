package com.remotedata.di

import org.koin.dsl.module

val appModules = listOf(
    networkModule,
    dataSourceModule
)
