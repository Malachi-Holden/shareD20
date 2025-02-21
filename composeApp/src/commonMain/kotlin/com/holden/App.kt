package com.holden

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.uuid.ExperimentalUuidApi
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalUuidApi::class)
@Composable
@Preview
fun App() {
    MaterialTheme {
        val client = remember { createHttpClient() }
        val viewModel: D20ViewModel = viewModel { D20ViewModel(ClientRepository(client)) }
        GamePage(viewModel)
    }
}