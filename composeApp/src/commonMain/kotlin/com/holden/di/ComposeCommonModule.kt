package com.holden.di

import com.holden.D20ViewModel
import com.holden.createHttpClient
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val composeCommonModule = module {
    single { createHttpClient() }
    viewModelOf<D20ViewModel>(constructor = { D20ViewModel() })
}

fun initKoin() = startKoin {
    modules(composeCommonModule)
}