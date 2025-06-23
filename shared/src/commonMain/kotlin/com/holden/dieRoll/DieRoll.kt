package com.holden.dieRoll

import kotlinx.serialization.Serializable

enum class DieRollVisibility(val title: String) {
    All("All"), BlindDM("Blind DM"), PrivateDM("Private DM")
}

@Serializable
data class DieRollForm(
    val gameCode: String,
    val rolledBy: Int,
    val value: Int,
    val visibility: DieRollVisibility,
    val fromDM: Boolean
) {
    fun toDieRoll(id: Int) = DieRoll(id, gameCode, rolledBy, value, visibility, fromDM)
}

@Serializable
data class DieRoll(
    val id: Int,
    val gameCode: String,
    val rolledBy: Int,
    val value: Int,
    val visibility: DieRollVisibility,
    val fromDM: Boolean
) {
    fun normalPlayerCanSee(playerId: Int): Boolean = when(visibility) {
        DieRollVisibility.All -> true
        DieRollVisibility.BlindDM -> false
        DieRollVisibility.PrivateDM -> rolledBy == playerId
    }
}