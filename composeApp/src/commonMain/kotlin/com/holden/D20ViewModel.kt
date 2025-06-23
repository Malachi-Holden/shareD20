package com.holden

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.holden.dieRoll.DieRollForm
import com.holden.dieRoll.DieRollVisibility
import com.holden.game.Game
import com.holden.game.GameForm
import com.holden.dm.DM
import com.holden.game.allPlayersFlow
import com.holden.player.Player
import com.holden.player.PlayerForm
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.io.IOException
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.random.Random

class D20ViewModel: ViewModel(), KoinComponent {
    val repository: D20Repository by inject()

    private val _appState = MutableStateFlow<AppState>(AppState.Home)
    val appState: AppState
        @Composable
        get() = _appState.collectAsState().value

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _currentPlayers = _appState
        .flatMapLatest { currentPlayersByAppState(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, listOf())

    val currentPlayers: List<Player>
        @Composable
        get() = _currentPlayers.collectAsState().value

    fun getCurrentAppState() = _appState.value
    fun getCurrentPlayers() = _currentPlayers.value

    fun onJoin(form: PlayerForm) = viewModelScope.async {
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

    fun onCreateGame(form: GameForm) = viewModelScope.async {
        val game: Game = repository.gamesRepository.create(form)
        val dm = game.dm
        goToDMingGame(dm, game)
    }

    fun rollDie(player: Player, game: Game, visibility: DieRollVisibility) = viewModelScope.async {
        val dieRoll = Random.nextInt(1, 20)
        repository.dieRollsRepository.create(
            DieRollForm(
                game.code,
                player.id,
                dieRoll,
                visibility,
                player.id == game.dm.playerId
            )
        )
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

    fun currentPlayersByAppState(appState: AppState): Flow<List<Player>> = when (appState) {
        is AppState.PlayingGame -> repository.gamesRepository.allPlayersFlow(game = appState.game)
        is AppState.DMingGame -> repository.gamesRepository.allPlayersFlow(game = appState.game)

        else -> MutableStateFlow(listOf())
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