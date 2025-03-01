package com.holden.player

import com.holden.CrdRepository
import com.holden.dieRoll.DieRoll

interface PlayersRepository: CrdRepository<Int, PlayerForm, Player> {
    suspend fun retreiveDieRolls(playerId: Int): List<DieRoll>
}