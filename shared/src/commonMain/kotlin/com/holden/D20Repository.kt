package com.holden

import com.holden.dieRoll.DieRoll
import com.holden.dieRoll.DieRollForm
import com.holden.games.Game
import com.holden.games.GameForm
import com.holden.dms.DM
import com.holden.dms.DMForm
import com.holden.players.Player
import com.holden.players.PlayerForm

interface D20Repository {
    val gamesRepository: CrdRepository<String, GameForm, Game>
    val playersRepository: CrdRepository<Int, PlayerForm, Player>
    val dmsRepository: CrdRepository<Int, Pair<DMForm, String>, DM>
    val dieRollsRepository: CrdRepository<Int, DieRollForm, DieRoll>
}