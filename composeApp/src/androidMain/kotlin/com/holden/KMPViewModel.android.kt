package com.holden

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

actual open class KMPViewModel: ViewModel()

@Composable
actual inline fun <reified VM: KMPViewModel>createViewModel(crossinline factory: () -> VM): VM {
    return viewModel { factory() }
}