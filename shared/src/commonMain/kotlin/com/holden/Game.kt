package com.holden

import kotlinx.serialization.Serializable

@Serializable
data class Game(val code: String, val name: String)

@Serializable
data class GameForm(val name: String) {
    fun toGame(code: String) = Game(code, name)
}