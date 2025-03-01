package com.holden.game

import com.holden.dm.DM
import com.holden.dm.DMForm
import kotlinx.serialization.Serializable

@Serializable
data class Game(
    val code: String,
    val name: String,
    val dm: DM
)

@Serializable
data class GameForm(val name: String, val dm: DMForm)