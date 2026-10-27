package com.jetbrains.kmpapp.di

import com.jetbrains.kmpapp.Greeting
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

val appModule = module {
    single { Greeting() }
}

fun initKoin() = initKoin(emptyList())

fun initKoin(extraModules: List<Module>) {
    startKoin {
        modules(
            appModule,
            *extraModules.toTypedArray(),
        )
    }
}
