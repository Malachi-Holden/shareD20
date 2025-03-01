package com.holden.game

import com.holden.CrdRepository
import com.holden.player.Player

interface GamesRepository: CrdRepository<String, GameForm, Game> {
    suspend fun retreivePlayers(code: String): List<Player>
}