package com.develop.core.navigation.di

import org.koin.dsl.module

/**
 * Koin-модуль для навигации.
 * В Navigation 3 навигация управляется через backStack напрямую в App.kt.
 */
val navigationModule = module {
    // Пустой модуль — навигация управляется через backStack в App.kt
}
