package com.holden

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Composable
@Preview
fun App() {
    // test code for latest server changes
//    val client = remember { createHttpClient() }
    MaterialTheme {
        GamePage()
//        var gameSetFromServer by remember { mutableStateOf<Game?>(null) }
//        val serverScope = rememberCoroutineScope()
//        val (gameTitle, setGameTitle) = remember { mutableStateOf("") }
//        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
//            TextField(gameTitle, setGameTitle)
//            Button(onClick = {
//                serverScope.launch {
//                    gameSetFromServer = client.post("/games") {
//                        contentType(ContentType.Application.Json)
//                        setBody(GameForm(name = gameTitle, dm = PlayerForm("", false, null)))
//                    }.body()
//                }
//            }) {
//                Text("create game $gameTitle")
//            }
//            Button(onClick = {
//                serverScope.launch {
//                    client.delete("/games/${gameSetFromServer?.code}")
//                }
//            }) {
//                Text("remove ${gameSetFromServer?.code}")
//            }
//            var gameFromServer by remember { mutableStateOf<Game?>(null) }
//            Button(onClick = {
//                serverScope.launch {
//                    try {
//                        gameFromServer = client.get("/games/${gameSetFromServer?.code}").body()
//                    } catch (e: NoTransformationFoundException) {
//                        println("error: ${e.message}")
//                    }
//                }
//            }) {
//                Text("Get game: ${gameSetFromServer?.code}")
//            }
//            Text("the game: ${gameFromServer?.name}")
//        }
    }
}