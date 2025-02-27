package com.holden.players

import kotlinx.serialization.Serializable

@Serializable
data class Player(
    val id: Int,
    val name: String,
    val gameCode: String
)

@Serializable
data class PlayerForm(
    val name: String,
    val gameCode: String
) {
    fun toPlayer(id: Int) = Player(id, name, gameCode)
}

