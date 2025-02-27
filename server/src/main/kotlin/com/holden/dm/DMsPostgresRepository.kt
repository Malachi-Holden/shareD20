package com.holden.dm

import com.holden.DMsRepository
import com.holden.InvalidDMId
import com.holden.InvalidGameCode
import com.holden.game.GameEntity
import org.jetbrains.exposed.sql.transactions.transaction

class DMsPostgresRepository: DMsRepository {
    override suspend fun create(form: Pair<DMForm, String>): DM = transaction {
        // this happens automatically when a game is created
        val (dm, code) = form
        val newGame = GameEntity.findById(code) ?: throw InvalidGameCode(code)
        DMEntity.new {
            name = dm.name
            game = newGame
        }.toModel()
    }

    override suspend fun read(id: Int): DM = transaction {
        DMEntity
            .findById(id)
            ?.toModel()
            ?: throw InvalidDMId(id)
    }

    override suspend fun delete(id: Int) {
        // No op
        // this happens automatically when a Game is created
    }
}