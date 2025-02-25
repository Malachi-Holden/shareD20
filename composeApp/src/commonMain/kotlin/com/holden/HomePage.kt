package com.holden

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import com.holden.dm.CreateGame
import com.holden.player.JoinGame
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.launch

/**
 * Sample view for the app main page
 */
@Composable
fun GamePage(
    viewModel: D20ViewModel
) {
    when (val appState = viewModel.appState) {
        is AppState.Home -> Home(
            goToCreate = viewModel::goToCreateGame,
            goToJoin = viewModel::goToJoinGame
        )
        is AppState.JoinGame -> JoinGame(onJoin = viewModel::onJoin)
        is AppState.CreateGame -> CreateGame(onCreateGame = viewModel::onCreateGame)
        is AppState.PlayingGame -> {
            PlayingGame(appState.player, appState.game)
        }
        is AppState.DMingGame -> {
            DMingGame(appState.dm, appState.game)
        }
        is AppState.ErrorState -> {
            Text("Error: ${appState.error.message}")
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