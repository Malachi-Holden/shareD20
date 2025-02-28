package com.holden.game

import com.holden.dieRoll.DieRoll
import com.holden.dm.DM
import com.holden.dm.DMForm
import com.holden.player.Player
import kotlinx.serialization.Serializable

@Serializable
data class Game(
    val code: String,
    val name: String,
    val dm: DM,
    val players: List<Player>
)

@Serializable
data class GameForm(val name: String, val dm: DMForm)