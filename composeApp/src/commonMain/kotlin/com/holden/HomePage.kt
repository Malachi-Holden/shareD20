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
    Home, JoinGame, CreateGame, PlayingGame, DMingGame
}

/**
 * Sample view for the app main page
 */
@Composable
fun GamePage() {
    val client = remember { createHttpClient() }
    val serverScope = rememberCoroutineScope()
    var appState by remember { mutableStateOf(AppState.Home) }
    var player: Player? by remember { mutableStateOf(null) }
    var dm: DM? by remember { mutableStateOf(null) }
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
                dm = game?.dm
                appState = AppState.DMingGame
            }
        })
        AppState.PlayingGame -> {
            PlayingGame(player ?: return, game ?: return)
        }
        AppState.DMingGame -> {
            DMingGame(dm ?: return, game ?: return)
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
fun DMingGame(
    dm: DM,
    game: Game
) {
    Column {
        Text("Welcome ${dm.name}! You are dming game ${game.name}")
        Text("Game code: ${game.code}")
    }
}

@Composable
fun PlayingGame(
    player: Player,
    game: Game
) {
    Column {
        Text("Welcome ${player.name}! You are playing game ${game.name}")
        Text("Game code: ${game.code}")
    }
}