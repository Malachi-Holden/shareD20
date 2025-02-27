package com.holden.dms

import com.holden.CrdRepository
import com.holden.InvalidDMId
import com.holden.InvalidGameCode
import com.holden.games.GameEntity
import org.jetbrains.exposed.sql.transactions.transaction

class DMsPostgresRepository: CrdRepository<Int, Pair<DMForm, String>, DM> {
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