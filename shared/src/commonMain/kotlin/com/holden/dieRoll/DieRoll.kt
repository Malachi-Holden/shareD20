package com.holden.dieRoll

import kotlinx.serialization.Serializable

enum class DieRollVisibility{
    All, BlindDM, PrivateDM
}

@Serializable
data class DieRollForm(
    val gameCode: String,
    val value: Int,
    val visibility: DieRollVisibility
)

@Serializable
data class DieRoll(
    val id: Int,
    val gameCode: String,
    val value: Int,
    val visibility: DieRollVisibility
)