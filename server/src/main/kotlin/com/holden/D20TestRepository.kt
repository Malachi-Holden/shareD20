package com.holden

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
    private val generateCodes: Iterator<String> = generateSequentialGameCodes()
    private val generatePlayerIds: Iterator<Int> = generateSequentialIds().iterator()

    override fun addGame(form: GameForm): Game {
        val code = generateCodes.next()
        val newGame = Game(code, form.name, listOf())
        games[code] = newGame
        val dm = form.dm.copy(gameCode = code)
        createPlayer(dm)
        return games[code] ?: newGame
    }

    override fun deleteGame(code: String?) {
        games.remove(code) ?: InvalidGameCode(code)
    }

    override fun getGameByCode(code: String?): Game {
        return games[code] ?: throw InvalidGameCode(code)
    }

    override fun addPlayerToGame(playerId: Int?, gameCode: String?) {
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
        val player = Player(id, form.name, form.isDM, form.gameCode)
        players[id] = player
        addPlayerToGame(id, form.gameCode)
        return players[id] ?: player
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
}
