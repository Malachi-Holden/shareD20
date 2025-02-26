package com.holden

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import io.ktor.client.*
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Composable
@Preview
fun App() {
    MaterialTheme {
        val client: HttpClient = koinInject()
        val viewModel: D20ViewModel = viewModel { D20ViewModel(ClientRepository(client)) }
        GamePage(viewModel)
    }
}