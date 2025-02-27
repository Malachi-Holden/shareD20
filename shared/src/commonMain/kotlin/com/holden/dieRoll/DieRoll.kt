package com.holden.dieRoll

import kotlinx.serialization.Serializable

@Serializable
data class DieRollForm(
    val value: Int,
    val gameCode: String
)

@Serializable
data class DieRoll(
    val id: Int,
    val value: Int,
    val gameCode: String
)