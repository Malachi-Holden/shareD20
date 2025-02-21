package com.holden

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class D20ViewModel(
    val repository: D20Repository
): ViewModel() {
    private val _appState = MutableStateFlow<AppState>(AppState.Home)
    val appState: AppState
        @Composable
        get() = _appState.collectAsState().value

    fun onJoin(form: PlayerForm) {
        viewModelScope.launch {
            val playerFromServer: Player = repository.createPlayer(form)
            val game: Game = repository.getGameByCode(form.gameCode)
            goToPlayingGame(playerFromServer, game)
        }
    }

    fun onCreateGame(form: GameForm) {
        viewModelScope.launch {
            val game: Game = repository.addGame(form)
            val dm = game.dm
            goToDMingGame(dm, game)
        }
    }

    fun goToCreateGame() {
        _appState.value = AppState.CreateGame
    }

    fun goToJoinGame() {
        _appState.value = AppState.JoinGame
    }

    fun goToPlayingGame(player: Player?, game: Game?) {
        if (player == null || game == null) {
            goToFailedState()
            return
        }
        _appState.value = AppState.PlayingGame(player, game)
    }

    fun goToDMingGame(dm: DM?, game: Game?) {
        if (dm == null || game == null) {
            goToFailedState()
            return
        }
        _appState.value = AppState.DMingGame(dm, game)
    }

    fun goToFailedState() {

        _appState.value = AppState.FailedState
    }
}

sealed class AppState {
    object Home: AppState()
    object JoinGame: AppState()
    object CreateGame: AppState()
    data class PlayingGame(val player: Player, val game: Game): AppState()
    data class DMingGame(val dm: DM, val game: Game): AppState()
    object FailedState: AppState()
}