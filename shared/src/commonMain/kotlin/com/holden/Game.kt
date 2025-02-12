package com.holden

import kotlinx.serialization.Serializable

@Serializable
data class Game(val id: String? = null, val name: String)
