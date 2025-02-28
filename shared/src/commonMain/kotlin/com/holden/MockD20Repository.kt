package com.holden

import com.holden.dieRoll.DieRoll
import com.holden.dieRoll.MockDieRollsRepository
import com.holden.dm.DM
import com.holden.dm.DMForm
import com.holden.dm.MockDMsRepository
import com.holden.game.MockGamesRepository
import com.holden.player.MockPlayersRepository
import com.holden.player.Player

fun generateSequentialIds(): Sequence<Int> {
    var current = 0
    return generateSequence {
        (current ++)
    }
}

class MockD20Repository(
    delayMS: Long = 0
): D20Repository {
    override val gamesRepository = MockGamesRepository(
        delayMS = delayMS,
        createDM = ::createDM,
        removePlayersInGame = ::removePlayersInGame,
        removeDMForGame = ::removeDMForGame
    )
    override val playersRepository = MockPlayersRepository(
        delayMS = delayMS,
        addPlayerToGame = ::addPlayerToGame,
        removePlayerFromGame = ::removePlayerFromGame
    )
    override val dmsRepository = MockDMsRepository(delayMS = delayMS)
    override val dieRollsRepository = MockDieRollsRepository(
        delayMS = delayMS
    )

    suspend fun createDM(form: Pair<DMForm, String>): DM {
        return dmsRepository.create(form)
    }

    fun removePlayersInGame(gameCode: String) {
        playersRepository.deletePlayersInGame(gameCode)
    }

    fun removePlayerFromGame(playerId: Int, gameCode: String) {
        gamesRepository.removePlayerFromGame(playerId, gameCode)
    }

    fun removeDMForGame(gameCode: String) {
        dmsRepository.removeDMForGame(gameCode)
    }

    fun addPlayerToGame(player: Player, gameCode: String) {
        gamesRepository.addPlayerToGame(player, gameCode)
    }
}