package com.holden

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.io.IOException

class D20ViewModel(
    val repository: D20Repository
): ViewModel() {
    private val _appState = MutableStateFlow<AppState>(AppState.Home)
    val appState: AppState
        @Composable
        get() = _appState.collectAsState().value

    fun getCurrentAppState() = _appState.value

    fun onJoin(form: PlayerForm) {
        viewModelScope.launch {
            try {
                val playerFromServer: Player = repository.createPlayer(form)
                val game: Game = repository.getGameByCode(form.gameCode)
                goToPlayingGame(playerFromServer, game)
            } catch (e: InvalidGameCode) {
                _appState.value = AppState.ErrorState(e)
            } catch (e: IOException) {
                _appState.value = AppState.ErrorState(e)
            }
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

    fun goToPlayingGame(player: Player, game: Game) {
        _appState.value = AppState.PlayingGame(player, game)
    }

    fun goToDMingGame(dm: DM, game: Game) {
        _appState.value = AppState.DMingGame(dm, game)
    }
}

sealed class AppState {
    object Home: AppState()
    object JoinGame: AppState()
    object CreateGame: AppState()
    data class PlayingGame(val player: Player, val game: Game): AppState()
    data class DMingGame(val dm: DM, val game: Game): AppState()
    data class ErrorState(val error: Exception): AppState()
}