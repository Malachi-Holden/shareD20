package com.holden.player

import com.holden.CrdRepository
import com.holden.dieRoll.DieRoll

interface PlayersRepository: CrdRepository<Int, PlayerForm, Player> {
    suspend fun retrieveDieRolls(playerId: Int): List<DieRoll>
    suspend fun retrieveVisibleDieRolls(playerId: Int): List<DieRoll>
}