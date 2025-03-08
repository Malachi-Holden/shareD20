package com.holden.game

import com.holden.GenerateCodes
import com.holden.InvalidGameCode
import com.holden.dm.DMEntity
import com.holden.player.Player
import com.holden.player.PlayerEntity
import com.holden.player.PlayersTable
import com.holden.player.toModel
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GamesPostgresRepository: GamesRepository, KoinComponent {
    private val generateCodes: GenerateCodes by inject()
    override suspend fun create(form: GameForm): Game = transaction {
        val code = generateCodes.next()
        val newGame = GameEntity.new(code) {
            name = form.name
        }
        val dmPlayer = PlayerEntity.new {
            name = form.dm.name
            game = newGame
        }
        DMEntity.new {
            name = form.dm.name
            game = newGame
            player = dmPlayer
        }
        newGame.toModel()
    }

    override suspend fun retrieve(id: String): Game = transaction {
        GameEntity
            .findById(id.uppercase())
            ?.toModel()
            ?: throw InvalidGameCode(id)
    }

    override suspend fun delete(id: String) = transaction {
        val game = GameEntity.findById(id.uppercase())
        game?.delete() ?: throw InvalidGameCode(id)
    }

    override suspend fun retreivePlayers(code: String): List<Player> = transaction {
        val game = GameEntity.findById(code) ?: throw InvalidGameCode(code)
        val result = game.players.map { it.toModel() }
        println("the games: ${result.joinToString()}")
        result
    }
}