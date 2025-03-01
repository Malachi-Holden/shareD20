package com.holden

import com.holden.dieRoll.DieRoll
import com.holden.dieRoll.MockDieRollsRepository
import com.holden.dm.DM
import com.holden.dm.DMForm
import com.holden.dm.MockDMsRepository
import com.holden.game.MockGamesRepository
import com.holden.player.MockPlayersRepository
import com.holden.player.Player
import com.holden.player.PlayerForm

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
        removeDMForGame = ::removeDMForGame,
        retreivePlayersFromRepo = ::retreivePlayersFromPlayerRepo
    )

    override val playersRepository = MockPlayersRepository(
        delayMS = delayMS,
        gameExists = ::gameExists,
        retrieveDieRollsFromRepo = ::retrieveDieRollsFromRepo
    )

    override val dmsRepository = MockDMsRepository(
        delayMS = delayMS,
        createPlayer = ::createPlayer
    )

    override val dieRollsRepository = MockDieRollsRepository(
        delayMS = delayMS,
        gameExists = ::gameExists,
        playerExists = ::playerExists
    )

    suspend fun createDM(form: Pair<DMForm, String>): DM {
        return dmsRepository.create(form)
    }

    fun removePlayersInGame(gameCode: String) {
        playersRepository.deletePlayersInGame(gameCode)
    }

    suspend fun createPlayer(form: DMForm, gameCode: String): Player {
        return playersRepository.create(PlayerForm(form.name, gameCode))
    }

    fun removeDMForGame(gameCode: String) {
        dmsRepository.removeDMForGame(gameCode)
    }

    suspend fun retreivePlayersFromPlayerRepo(gameCode: String): List<Player> {
        return playersRepository.players.filterValues { it.gameCode == gameCode }.values.toList()
    }

    fun gameExists(gameCode: String): Boolean {
        return gamesRepository.games.containsKey(gameCode)
    }

    fun playerExists(id: Int): Boolean {
        return playersRepository.players.containsKey(id)
    }

    fun retrieveDieRollsFromRepo(id: Int): List<DieRoll> {
        return dieRollsRepository.dieRolls.filterValues { it.rolledBy == id }.values.toList()
    }
}