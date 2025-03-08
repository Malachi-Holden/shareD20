package com.holden

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.holden.dm.CreateGame
import com.holden.game.Game
import com.holden.player.JoinGame
import com.holden.dm.DM
import com.holden.player.Player
import org.koin.compose.viewmodel.koinViewModel

/**
 * Sample view for the app main page
 */
@Composable
fun GamePage(
    viewModel: D20ViewModel = koinViewModel()
) {
    when (val appState = viewModel.appState) {
        is AppState.Home -> Home(
            goToCreate = viewModel::goToCreateGame,
            goToJoin = viewModel::goToJoinGame
        )
        is AppState.JoinGame -> JoinGame(onJoin = viewModel::onJoin)
        is AppState.CreateGame -> CreateGame(onCreateGame = viewModel::onCreateGame)
        is AppState.PlayingGame -> {
            PlayingGame(appState.player, appState.game, viewModel.currentPlayers)
        }
        is AppState.DMingGame -> {
            DMingGame(appState.dm, appState.game, viewModel.currentPlayers)
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
    game: Game,
    allPlayers: List<Player>
) {
    Column {
        Text("Welcome ${dm.name}! You are dming game ${game.name}")
        Text("Game code: ${game.code}")
        Text("All players:")
        LazyColumn {
            items(allPlayers) {
                Text(it.name)
            }
        }
    }
}

@Composable
fun PlayingGame(
    player: Player,
    game: Game,
    allPlayers: List<Player>
) {
    Column {
        Text("Welcome ${player.name}! You are playing game ${game.name}")
        Text("Game code: ${game.code}")
        Text("All players:")
        LazyColumn {
            items(allPlayers) {
                Text(it.name)
            }
        }
    }
}