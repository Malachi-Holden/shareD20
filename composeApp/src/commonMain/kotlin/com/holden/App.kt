package com.holden

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Composable
@Preview
fun App() {
    MaterialTheme {
        val x = D20ViewModel()
        GamePage()
    }
}