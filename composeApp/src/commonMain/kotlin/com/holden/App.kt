package com.holden

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import shared20.composeapp.generated.resources.Res
import shared20.composeapp.generated.resources.compose_multiplatform

@Composable
@Preview
fun App() {
    val client = remember { createHttpClient() }
    MaterialTheme {
        var resultFromServer by remember { mutableStateOf("") }
        val serverScope = rememberCoroutineScope()
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = {
                serverScope.launch {
                    resultFromServer = client.get("/").body()
                }
            }) {
                Text("Click me!")
            }
            Text("the result: $resultFromServer")
        }
    }
}