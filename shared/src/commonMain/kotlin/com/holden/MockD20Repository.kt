package com.holden

import com.holden.dieRoll.DieRoll
import com.holden.dieRoll.MockDieRollsRepository
import com.holden.dms.DM
import com.holden.dms.DMForm
import com.holden.dms.MockDMsRepository
import com.holden.games.MockGamesRepository
import com.holden.players.MockPlayersRepository
import com.holden.players.Player

fun generateSequentialIds(): Sequence<Int> {
    var current = 0
    return generateSequence {
        (current ++)
    }
}

class MockD20Repository: D20Repository {
    override val gamesRepository = MockGamesRepository(
        createDM = ::createDM,
        removePlayersInGame = ::removePlayersInGame,
        removeDMForGame = ::removeDMForGame
    )
    override val playersRepository = MockPlayersRepository(
        addPlayerToGame = ::addPlayerToGame
    )
    override val dmsRepository = MockDMsRepository()
    override val dieRollsRepository = MockDieRollsRepository(
        addDieRollToGame = ::addDieRollToGame
    )

    suspend fun createDM(form: Pair<DMForm, String>): DM {
        return dmsRepository.create(form)
    }

    fun removePlayersInGame(gameCode: String) {
        playersRepository.deletePlayersInGame(gameCode)
    }

    fun removeDMForGame(gameCode: String) {
        dmsRepository.removeDMForGame(gameCode)
    }

    fun addPlayerToGame(player: Player, gameCode: String) {
        gamesRepository.addPlayerToGame(player, gameCode)
    }

    fun addDieRollToGame(dieRoll: DieRoll, gameCode: String) {
        gamesRepository.addDieRollToGame(dieRoll, gameCode)
    }
}