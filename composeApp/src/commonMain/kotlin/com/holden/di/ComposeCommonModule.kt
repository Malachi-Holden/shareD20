package com.holden.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.holden.createHttpClient
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.dsl.module

val composeCommonModule = module {
    single { createHttpClient() }
}

fun initKoin() = startKoin {
    modules(composeCommonModule)
}

//val LocalKoin = staticCompositionLocalOf<Koin> {
//    error("LocalKoin can only be accessed from inside KoinComposeApp context")
//}
//
//@Composable
//inline fun <reified T: Any> getFromKoin() = LocalKoin.current.get<T>()
//
//@Composable
//fun KoinComposeApp(
//    koin: Koin = initKoin(),
//    content: @Composable () -> Unit
//) {
//    CompositionLocalProvider(LocalKoin provides koin, content)
//}