package com.holden

import kotlinx.serialization.Serializable

@Serializable
data class Game(
    val code: String,
    val name: String,
    val players: List<Player>
)

@Serializable
data class GameForm(val name: String, val dm: PlayerForm)