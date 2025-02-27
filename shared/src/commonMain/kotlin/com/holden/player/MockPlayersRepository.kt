package com.holden.player

import com.holden.InvalidPlayerId
import com.holden.PlayersRepository
import com.holden.game.Game
import com.holden.generateSequentialIds
import com.holden.util.removeAll
import kotlinx.coroutines.delay

class MockPlayersRepository(
    val delayMS: Long = 0,
    val addPlayerToGame: (player: Player, gameCode: String) -> Unit
): PlayersRepository {
    private val generatePlayerIds: Iterator<Int> = generateSequentialIds().iterator()
    val games: MutableMap<String, Game> = mutableMapOf() // remove
    val players: MutableMap<Int, Player> = mutableMapOf()

    override suspend fun create(form: PlayerForm): Player {
        delay(delayMS)
        val id = generatePlayerIds.next()
        val player = Player(id, form.name, form.gameCode)
        players[id] = player
        addPlayerToGame(player, form.gameCode)
        return player
    }

    override suspend fun read(id: Int): Player {
        delay(delayMS)
        return players[id] ?: throw InvalidPlayerId(id)
    }

    override suspend fun delete(id: Int) {
        delay(delayMS)
        players.remove(id) ?: throw InvalidPlayerId(id)
    }

    fun deletePlayersInGame(gameCode: String) {
        players.removeAll { _, player -> player.gameCode == gameCode }
    }
}