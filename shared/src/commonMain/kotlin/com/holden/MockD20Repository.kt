package com.holden

import com.holden.dieRoll.MockDieRollsRepository
import com.holden.dms.MockDMsRepository
import com.holden.games.MockGamesRepository
import com.holden.players.MockPlayersRepository

fun generateSequentialIds(): Sequence<Int> {
    var current = 0
    return generateSequence {
        (current ++)
    }
}

class MockD20Repository: D20Repository {
    lateinit var mockGamesRepository: MockGamesRepository
    lateinit var mockPlayersRepository: MockPlayersRepository
    lateinit var mockDMsRepository: MockDMsRepository
    lateinit var mockDieRollsRepository: MockDieRollsRepository
    init {
        mockGamesRepository = MockGamesRepository(
            createDM = mockDMsRepository::create,
            removePlayersInGame = mockPlayersRepository::deletePlayersInGame,
            removeDMForGame = mockDMsRepository::removeDMForGame
        )
        mockPlayersRepository = MockPlayersRepository(
            addPlayerToGame = mockGamesRepository::addPlayerToGame
        )
        mockDMsRepository = MockDMsRepository()
        mockDieRollsRepository = MockDieRollsRepository(
            addDieRollToGame = mockGamesRepository::addDieRollToGame
        )
    }

    override val gamesRepository = mockGamesRepository
    override val playersRepository = mockPlayersRepository
    override val dmsRepository = mockDMsRepository
    override val dieRollsRepository = mockDieRollsRepository
}