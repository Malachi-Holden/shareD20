package com.holden.game

import com.holden.GamesRepository
import com.holden.InvalidGameCode
import com.holden.dieRoll.DieRoll
import com.holden.dm.DM
import com.holden.dm.DMForm
import com.holden.generateSequentialIds
import com.holden.player.Player
import kotlinx.coroutines.delay

fun generateSequentialGameCodes() = generateSequentialIds().map { it.toString().padStart(8, '0') }.iterator()

class MockGamesRepository(
    val delayMS: Long = 0,
    val createDM: suspend (Pair<DMForm, String>) -> DM,
    val removePlayersInGame: suspend (gameCode: String) -> Unit,
    val removeDMForGame: suspend (gameCode: String) -> Unit
): GamesRepository {
    val games: MutableMap<String, Game> = mutableMapOf()
    private val generateCodes: Iterator<String> = generateSequentialGameCodes()

    override suspend fun create(form: GameForm): Game {
        delay(delayMS)
        val code = generateCodes.next()
        val dm = createDM(form.dm to code)
        val newGame = Game(code, form.name, dm, listOf(), listOf())
        games[code] = newGame
        return newGame
    }

    override suspend fun read(id: String): Game {
        delay(delayMS)
        return games[id] ?: throw InvalidGameCode(id)
    }

    override suspend fun delete(id: String) {
        delay(delayMS)
        games.remove(id) ?: throw InvalidGameCode(id)
        removePlayersInGame(id)
        removeDMForGame(id)
    }

    fun addPlayerToGame(player: Player, gameCode: String) {
        val game = games[gameCode] ?: throw InvalidGameCode(gameCode)
        games[gameCode] = game.copy(
            players = game.players + player
        )
    }

    fun addDieRollToGame(dieRoll: DieRoll, gameCode: String) {
        val game = games[gameCode] ?: throw InvalidGameCode(gameCode)
        games[gameCode] = game.copy(
            dieRolls = game.dieRolls + dieRoll
        )
    }
}