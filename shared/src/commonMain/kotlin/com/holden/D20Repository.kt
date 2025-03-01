package com.holden

import com.holden.dieRoll.DieRollsRepository
import com.holden.dm.DMsRepository
import com.holden.game.GamesRepository
import com.holden.player.PlayersRepository

interface D20Repository {
    val gamesRepository: GamesRepository
    val playersRepository: PlayersRepository
    val dmsRepository: DMsRepository
    val dieRollsRepository: DieRollsRepository
}

fun interface GenerateCodes {
    fun next(): String
}