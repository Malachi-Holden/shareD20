package com.holden

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
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Composable
@Preview
fun App() {
    val client = remember { createHttpClient() }
    MaterialTheme {
        var gameFromServer by remember { mutableStateOf<Game?>(null) }
        val serverScope = rememberCoroutineScope()
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = {
                serverScope.launch {
                    gameFromServer = client.post("/games") {
                        contentType(ContentType.Application.Json)
                        setBody(Game(name = "my game"))
                    }.body()
                }
            }) {
                Text("create game ${gameFromServer?.name}")
            }
            Button(onClick = {
                serverScope.launch {
                    client.delete("/games/${gameFromServer?.id}")
                }
            }) {
                Text("remove ${gameFromServer?.id}")
            }
            Button(onClick = {
                serverScope.launch {
                    try {
                        gameFromServer = client.get("/games/${gameFromServer?.id}").body()
                    } catch (e: NoTransformationFoundException) {
                        println("error: ${e.message}")
                    }
                }
            }) {
                Text("Get game: ${gameFromServer?.id}")
            }
            Text("the game: ${gameFromServer?.name}")
        }
    }
}