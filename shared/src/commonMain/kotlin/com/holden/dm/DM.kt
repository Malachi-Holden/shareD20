package com.holden.dm

import kotlinx.serialization.Serializable

@Serializable
data class DM(
    val id: Int,
    val playerId: Int,
    val name: String,
    val gameCode: String
)

@Serializable
data class DMForm(
    val name: String
)