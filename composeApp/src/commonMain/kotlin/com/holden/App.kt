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
import io.ktor.http.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Composable
@Preview
fun App() {
    val client = remember { createHttpClient() }
    MaterialTheme {
        var gameId by remember { mutableStateOf(Uuid.random().toHexString()) }
        var gameFromServer by remember { mutableStateOf<Game?>(null) }
        val serverScope = rememberCoroutineScope()
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = {
                serverScope.launch {
                    client.post("/games") {
                        contentType(ContentType.Application.Json)
                        setBody(Game(id = gameId))
                    }
                }
            }) {
                Text("create game $gameId")
            }
            Button(onClick = {
                serverScope.launch {
                    try {
                        gameFromServer = client.get("/games/$gameId").body()
                    } catch (e: NoTransformationFoundException) {
                        println("error: ${e.message}")
                    }
                }
            }) {
                Text("Get game $gameId")
            }
            Text("the game: ${gameFromServer?.id}")
        }
    }
}