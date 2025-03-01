package com.holden.di

import com.holden.*
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val composeCommonModule = module {
    single { createHttpClient() }
    single<D20Repository> { ClientRepository() }
    viewModelOf<D20ViewModel>(constructor = { D20ViewModel() })
}

fun initKoin() = startKoin {
    modules(composeCommonModule)
}