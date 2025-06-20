package com.holden.player

import com.holden.InvalidGameCode
import com.holden.InvalidPlayerId
import com.holden.dieRoll.DieRoll
import com.holden.generateSequentialIds
import com.holden.util.removeAll
import kotlinx.coroutines.delay

class MockPlayersRepository(
    val delayMS: Long = 0,
    val gameExists: (code: String) -> Boolean,
    val retrieveDieRollsFromRepo: (id: Int) -> List<DieRoll>,
    val retrieveVisibleDieRollsForPlayer: (id: Int) -> List<DieRoll>
): PlayersRepository {
    private val generatePlayerIds: Iterator<Int> = generateSequentialIds().iterator()
    val players: MutableMap<Int, Player> = mutableMapOf()

    override suspend fun create(form: PlayerForm): Player {
        delay(delayMS)
        if (!gameExists(form.gameCode)) throw InvalidGameCode(form.gameCode)
        val id = generatePlayerIds.next()
        val player = Player(id, form.name, form.gameCode)
        players[id] = player
        return player
    }

    override suspend fun retrieve(id: Int): Player {
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

    override suspend fun retrieveDieRolls(playerId: Int): List<DieRoll> {
        if (!players.containsKey(playerId)) throw InvalidPlayerId(playerId)
        return retrieveDieRollsFromRepo(playerId)
    }

    override suspend fun retrieveVisibleDieRolls(playerId: Int): List<DieRoll> {
        if (!players.containsKey(playerId)) throw InvalidPlayerId(playerId)
        return retrieveVisibleDieRollsForPlayer(playerId)
    }
}