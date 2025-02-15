package com.holden

import kotlinx.serialization.Serializable

@Serializable
data class Player(
    val id: Int,
    val name: String,
    val isDM: Boolean,
    val gameCode: String?
)

@Serializable
data class PlayerForm(
    val name: String,
    val isDM: Boolean,
    val gameCode: String?
) {
    fun toPlayer(id: Int) = Player(id, name, isDM, gameCode)
}