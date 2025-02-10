package com.holden

class InMemoryD20Repository: D20Repository {
    override val games: MutableMap<String, Game> = mutableMapOf()
}