package com.holden

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.uuid.ExperimentalUuidApi
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalUuidApi::class)
@Composable
@Preview
fun App() {
    MaterialTheme {
        val viewModel: D20ViewModel = viewModel { D20ViewModel(MockD20Repository()) }
        GamePage(viewModel)
    }
}