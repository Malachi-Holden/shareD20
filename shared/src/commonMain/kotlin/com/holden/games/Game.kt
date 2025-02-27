package com.holden.games

import com.holden.dieRoll.DieRoll
import com.holden.dms.DM
import com.holden.dms.DMForm
import com.holden.players.Player
import kotlinx.serialization.Serializable

@Serializable
data class Game(
    val code: String,
    val name: String,
    val dm: DM,
    val players: List<Player>,
    val dieRolls: List<DieRoll>
)

@Serializable
data class GameForm(val name: String, val dm: DMForm)