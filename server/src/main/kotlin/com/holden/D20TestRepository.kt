package com.holden

import com.holden.util.removeAll

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
class D20TestRepository: D20Repository {
    private val games: MutableMap<String, Game> = mutableMapOf()
    private val players: MutableMap<Int, Player> = mutableMapOf()
    private val dms: MutableMap<Int, DM> = mutableMapOf()
    private val generateCodes: Iterator<String> = generateSequentialGameCodes()
    private val generatePlayerIds: Iterator<Int> = generateSequentialIds().iterator()
    private val generateDMIds: Iterator<Int> = generateSequentialIds().iterator()

    override fun addGame(form: GameForm): Game {
        val code = generateCodes.next()
        val dm = form.dm.toDM(generateDMIds.next(), code)
        val newGame = Game(code, form.name, dm, listOf())
        games[code] = newGame
        dms[dm.id] = dm
        return newGame
    }

    override fun deleteGame(code: String?) {
        games.remove(code) ?: InvalidGameCode(code)
        players.removeAll { _, player -> player.gameCode == code }
        dms.removeAll { _, dm -> dm.gameCode == code }
    }

    override fun getGameByCode(code: String?): Game {
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

    override fun hasGameWithCode(code: String?): Boolean {
        return games.contains(code)
    }

    override fun createPlayer(form: PlayerForm): Player {
        val id = generatePlayerIds.next()
        val player = Player(id, form.name, form.gameCode)
        players[id] = player
        addPlayerToGame(id, form.gameCode)
        return player
    }

    override fun deletePlayer(id: Int?) {
        players.remove(id) ?: throw InvalidPlayerId(id)
    }

    override fun getPlayer(id: Int?): Player {
        return players[id] ?: throw InvalidPlayerId(id)
    }
    override fun hasPlayer(id: Int?): Boolean {
        return players.containsKey(id)
    }

    override fun getDM(id: Int?): DM {
        return dms[id] ?: throw InvalidDMId(id)
    }

    override fun hasDM(id: Int?): Boolean {
        return dms.containsKey(id)
    }
}
