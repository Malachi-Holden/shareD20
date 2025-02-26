package com.holden

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.holden.di.KoinComposeApp
import com.holden.di.composeCommonModule
import com.holden.di.initKoin
import org.koin.compose.KoinApplication

fun main() = application {
    KoinApplication(application = {
        modules(composeCommonModule)
    }) {
        Window(
            onCloseRequest = ::exitApplication,
            title = "shareD20",
        ) {
            App()
        }
    }
}