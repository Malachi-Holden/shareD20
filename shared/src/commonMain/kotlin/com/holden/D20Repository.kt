package com.holden

import com.holden.dieRoll.DieRoll
import com.holden.dieRoll.DieRollForm
import com.holden.game.Game
import com.holden.game.GameForm
import com.holden.dm.DM
import com.holden.dm.DMForm
import com.holden.player.Player
import com.holden.player.PlayerForm

typealias GamesRepository = CrdRepository<String, GameForm, Game>
typealias PlayersRepository = CrdRepository<Int, PlayerForm, Player>
typealias DMsRepository = CrdRepository<Int, Pair<DMForm, String>, DM>
typealias DieRollsRepository = CrdRepository<Int, DieRollForm, DieRoll>

interface D20Repository {
    val gamesRepository: GamesRepository
    val playersRepository: PlayersRepository
    val dmsRepository: DMsRepository
    val dieRollsRepository: DieRollsRepository
}

fun interface GenerateCodes {
    fun next(): String
}