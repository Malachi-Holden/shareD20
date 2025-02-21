package com.holden

import androidx.compose.runtime.Composable

expect open class KMPViewModel()

@Composable
expect inline fun<reified VM: KMPViewModel>createViewModel(crossinline factory: () -> VM): VM