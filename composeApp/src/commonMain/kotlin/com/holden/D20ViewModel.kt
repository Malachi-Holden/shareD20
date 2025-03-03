package com.holden

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.holden.game.Game
import com.holden.game.GameForm
import com.holden.dm.DM
import com.holden.player.Player
import com.holden.player.PlayerForm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.io.IOException
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class D20ViewModel: ViewModel(), KoinComponent {
    val repository: D20Repository by inject()

    private val _appState = MutableStateFlow<AppState>(AppState.Home)
    val appState: AppState
        @Composable
        get() = _appState.collectAsState().value

    fun getCurrentAppState() = _appState.value

    fun onJoin(form: PlayerForm) {
        viewModelScope.launch {
            try {
                val playerFromServer: Player = repository.playersRepository.create(form)
                val game: Game = repository.gamesRepository.retrieve(form.gameCode)
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
            val game: Game = repository.gamesRepository.create(form)
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