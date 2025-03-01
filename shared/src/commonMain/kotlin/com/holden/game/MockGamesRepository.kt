package com.holden.game

import com.holden.InvalidGameCode
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
    val removeDMForGame: suspend (gameCode: String) -> Unit,
    val retreivePlayersFromPlayerRepo: suspend (gameCode: String) -> List<Player>
): GamesRepository {
    val games: MutableMap<String, Game> = mutableMapOf()
    private val generateCodes: Iterator<String> = generateSequentialGameCodes()

    override suspend fun create(form: GameForm): Game {
        delay(delayMS)
        val code = generateCodes.next()
        val fakeDM = DM(-1, -1, "", code)
        val fakeGame = Game(code, form.name, fakeDM)
        games[code] = fakeGame
        val dm = createDM(form.dm to code)
        val actualGame = Game(code, form.name, dm)
        games[code] = actualGame
        return actualGame
    }

    override suspend fun retrieve(id: String): Game {
        delay(delayMS)
        return games[id] ?: throw InvalidGameCode(id)
    }

    override suspend fun delete(id: String) {
        delay(delayMS)
        games.remove(id) ?: throw InvalidGameCode(id)
        removePlayersInGame(id)
        removeDMForGame(id)
    }

    override suspend fun retreivePlayers(code: String): List<Player> = retreivePlayersFromPlayerRepo(code)
}