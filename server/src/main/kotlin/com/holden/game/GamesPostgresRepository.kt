package com.holden.game

import com.holden.CrdRepository
import com.holden.GenerateCodes
import com.holden.InvalidGameCode
import com.holden.dm.DMEntity
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GamesPostgresRepository: CrdRepository<String, GameForm, Game>, KoinComponent {
    private val generateCodes: GenerateCodes by inject()
    override suspend fun create(form: GameForm): Game = transaction {
        val code = generateCodes.next()
        val newGame = GameEntity.new(code) {
            name = form.name
        }
        DMEntity.new {
            name = form.dm.name
            game = newGame
        }
        newGame.toModel()
    }

    override suspend fun read(id: String): Game = transaction {
        GameEntity
            .findById(id.uppercase())
            ?.toModel()
            ?: throw InvalidGameCode(id)
    }

    override suspend fun delete(id: String) = transaction {
        val game = GameEntity.findById(id.uppercase())
        game?.delete() ?: throw InvalidGameCode(id)
    }
}