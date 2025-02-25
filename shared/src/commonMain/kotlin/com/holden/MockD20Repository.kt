package com.holden

import com.holden.util.removeAll
import kotlinx.coroutines.delay

fun generateSequentialIds(): Sequence<Int> {
    var current = 0
    return generateSequence {
        (current ++)
    }
}

fun generateSequentialGameCodes() = generateSequentialIds().map { it.toString().padStart(8, '0') }.iterator()

/**
 * Fake repository that uses an in memory hashmap to replicate database behavior
 */
class MockD20Repository(
    val delayMS: Long = 0
): D20Repository {
    val games: MutableMap<String, Game> = mutableMapOf()
    val players: MutableMap<Int, Player> = mutableMapOf()
    val dms: MutableMap<Int, DM> = mutableMapOf()
    private val generateCodes: Iterator<String> = generateSequentialGameCodes()
    private val generatePlayerIds: Iterator<Int> = generateSequentialIds().iterator()
    private val generateDMIds: Iterator<Int> = generateSequentialIds().iterator()

    override suspend fun addGame(form: GameForm): Game {
        delay(delayMS)
        val code = generateCodes.next()
        val dm = form.dm.toDM(generateDMIds.next(), code)
        val newGame = Game(code, form.name, dm, listOf())
        games[code] = newGame
        dms[dm.id] = dm
        return newGame
    }

    override suspend fun deleteGame(code: String?) {
        delay(delayMS)
        games.remove(code) ?: throw InvalidGameCode(code)
        players.removeAll { _, player -> player.gameCode == code }
        dms.removeAll { _, dm -> dm.gameCode == code }
    }

    override suspend fun getGameByCode(code: String?): Game {
        delay(delayMS)
        return games[code] ?: throw InvalidGameCode(code)
    }

    private fun addPlayerToGame(playerId: Int?, gameCode: String?) {
        if (playerId == null) throw InvalidPlayerId(null)
        if (gameCode == null) throw InvalidGameCode(null)
        val game = games[gameCode] ?: throw InvalidGameCode(gameCode)
        val player = players[playerId] ?: throw InvalidPlayerId(playerId)
        games[gameCode] = game.copy(
            players = game.players + player
        )
        players[playerId] = player.copy(
            gameCode = gameCode
        )
    }

    override suspend fun hasGameWithCode(code: String?): Boolean {
        delay(delayMS)
        return games.contains(code)
    }

    override suspend fun createPlayer(form: PlayerForm): Player {
        delay(delayMS)
        val id = generatePlayerIds.next()
        val player = Player(id, form.name, form.gameCode)
        players[id] = player
        addPlayerToGame(id, form.gameCode)
        return player
    }

    override suspend fun deletePlayer(id: Int?) {
        delay(delayMS)
        players.remove(id) ?: throw InvalidPlayerId(id)
    }

    override suspend fun getPlayer(id: Int?): Player {
        delay(delayMS)
        return players[id] ?: throw InvalidPlayerId(id)
    }
    override suspend fun hasPlayer(id: Int?): Boolean {
        delay(delayMS)
        return players.containsKey(id)
    }

    override suspend fun getDM(id: Int?): DM {
        delay(delayMS)
        return dms[id] ?: throw InvalidDMId(id)
    }

    override suspend fun hasDM(id: Int?): Boolean {
        delay(delayMS)
        return dms.containsKey(id)
    }
}
