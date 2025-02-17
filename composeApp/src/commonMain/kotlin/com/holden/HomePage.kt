package com.holden

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import com.holden.dm.CreateGame
import com.holden.player.JoinGame
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.launch

enum class AppState {
    Home, JoinGame, CreateGame, PlayingGame
}

@Composable
fun GamePage() {
    val client = remember { createHttpClient() }
    val serverScope = rememberCoroutineScope()
    var appState by remember { mutableStateOf(AppState.Home) }
    var player: Player? by remember { mutableStateOf(null) }
    var game: Game? by remember { mutableStateOf(null) }
    when (appState) {
        AppState.Home -> Home(
            goToCreate = { appState = AppState.CreateGame },
            goToJoin = { appState = AppState.JoinGame }
        )
        AppState.JoinGame -> JoinGame(onJoin = { form ->
            serverScope.launch {
                val playerFromServer: Player = client.post("/players") {
                    contentType(ContentType.Application.Json)
                    setBody(form)
                }.body()
                game = client.get("/games/${playerFromServer.gameCode}").body()
                player = playerFromServer
                appState = AppState.PlayingGame
            }
        })
        AppState.CreateGame -> CreateGame(onCreateGame = { form ->
            serverScope.launch {
                game = client.post("/games") {
                    contentType(ContentType.Application.Json)
                    setBody(form)
                }.body()
                player = game?.players?.first { it.isDM }
                appState = AppState.PlayingGame
            }
        })
        AppState.PlayingGame -> {
            PlayingGame(player ?: return, game ?: return)
        }
    }
}

@Composable
fun Home(
    goToCreate: () -> Unit,
    goToJoin: () -> Unit
) {
    Column {
        Button(onClick = goToCreate) {
            Text("Create Game")
        }
        Button(onClick = goToJoin) {
            Text("Join Game")
        }
    }
}

@Composable
fun PlayingGame(
    player: Player,
    game: Game
) {
    val verb = if (player.isDM) "dming" else "playing"
    Text("Welcome ${player.name}! You are $verb game ${game.code}")
}