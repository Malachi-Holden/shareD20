package com.holden

import com.holden.dieRoll.DieRoll
import com.holden.dieRoll.DieRollForm
import com.holden.game.Game
import com.holden.game.GameForm
import com.holden.dm.DM
import com.holden.dm.DMForm
import com.holden.player.Player
import com.holden.player.PlayerForm

interface D20Repository {
    val gamesRepository: CrdRepository<String, GameForm, Game>
    val playersRepository: CrdRepository<Int, PlayerForm, Player>
    val dmsRepository: CrdRepository<Int, Pair<DMForm, String>, DM>
    val dieRollsRepository: CrdRepository<Int, DieRollForm, DieRoll>
}

fun interface GenerateCodes {
    fun next(): String
}