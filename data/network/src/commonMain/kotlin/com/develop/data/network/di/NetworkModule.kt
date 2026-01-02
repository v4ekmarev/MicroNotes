package com.develop.data.network.di

import com.develop.data.network.api.HttpClientFactory
import org.koin.core.module.Module
import org.koin.dsl.module

expect fun getBaseUrl(): String

expect fun isDebug(): Boolean

expect val networkPlatformModule: Module

val networkModule = module {
    includes(networkPlatformModule)
    
    single { HttpClientFactory(get(), isDebug()) }
    single { get<HttpClientFactory>().create(getBaseUrl()) }
}
