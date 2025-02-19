package com.holden

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

@Serializable
data class DM(
    val id: Int,
    val name: String,
    val gameCode: String
)

@Serializable
data class DMForm(
    val name: String
) {
    fun toDM(id: Int, gameCode: String) = DM(id, name, gameCode)
}