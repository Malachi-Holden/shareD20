package com.holden

import androidx.compose.runtime.Composable

actual open class KMPViewModel

@Composable
actual inline fun <reified VM: KMPViewModel>createViewModel(crossinline factory: () -> VM): VM {
    return factory()
}