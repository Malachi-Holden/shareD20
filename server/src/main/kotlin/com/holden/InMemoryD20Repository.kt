package com.holden

import com.holden.util.uniqueRandomStringIterator

val GAME_ID_LENGTH = 8
class InMemoryD20Repository: D20Repository {
    private val games: MutableMap<String, Game> = mutableMapOf()
    private val generateIds = uniqueRandomStringIterator(GAME_ID_LENGTH) {
        games.contains(it)
    }
    override fun addGame(game: Game): Game {
        val id = generateIds.next()
        val newGame = game.copy(id = id)
        games[id] = newGame
        return newGame
    }

    override fun deleteGame(id: String?): Boolean {
        return games.remove(id) != null
    }

    override fun getGame(id: String?): Game? {
        return games[id]
    }

    override fun hasGame(id: String?): Boolean {
        return games.contains(id)
    }
}