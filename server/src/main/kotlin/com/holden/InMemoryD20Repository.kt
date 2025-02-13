package com.holden

import com.holden.util.uniqueRandomStringIterator

val GAME_ID_LENGTH = 8
class InMemoryD20Repository(
    private val games: MutableMap<String, Game> = mutableMapOf(),
    private val generateCodes: Iterator<String> = uniqueRandomStringIterator(GAME_ID_LENGTH) {
        games.contains(it)
    }
): D20Repository {


    override fun addGame(form: GameForm): Game {
        val code = generateCodes.next()
        val newGame = form.toGame(code)
        games[code] = newGame
        return newGame
    }

    override fun deleteGame(code: String?): Boolean {
        return games.remove(code) != null
    }

    override fun getGameByCode(code: String?): Game? {
        return games[code]
    }

    override fun hasGameWithCode(code: String?): Boolean {
        return games.contains(code)
    }
}